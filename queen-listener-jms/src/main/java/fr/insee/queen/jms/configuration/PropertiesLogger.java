package fr.insee.queen.jms.configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;

import java.util.*;
import java.util.function.Supplier;

/**
 * Listener Spring permettant d'afficher les props dans les logs au démarrage de l'application : se déclenche
 * sur l'évènement ApplicationEnvironmentPreparedEvent.
 *
 * Par défaut affiche les propriétés qui vérifient les conditions suivantes :
 * 1. passées soit par la ligne de commande (<code>--maProp=valeur</code>), soit présentes dans les fichiers properties
 * détectés par Spring boot au démarrage (une propriété présente uniquement dans le code sous la forme
 * <code>@Value("${ma.propriete}")</code> dont la valeur serait passée uniquement par variable d'environnement ne sera
 * donc pas détectée. Pour qu'elle le soit, il faut la faire figurer dans le fichier application.properties par exemple)
 * 2. qui sont préfixées par l'un des éléments de PropertiesLogger.prefixesAffichesParDefaut
 *
 * Pour chaque propriété affichée :
 * - si la clé contient l'un des mots parmi PropertiesLogger.motsCachesParDefaut, affiche alors "****"
 * - sinon c'est la valeur de la propriété telle qu'elle sera résolue par Spring boot dans l'application qui est affichée
 *
 * Enfin, sont loguées également les noms des PropertySource de Spring dont proviennent les clés des propriétés qui sont affichées
 *
 * Le comportement de cette classe peut être amendé par les propriétés suivantes :
 * - fr.insee.properties.log.key.select : stratégie de sélection des sources de propriétés qui seront affichées :
 *   - trois valeurs possibles :
 *     - ALL(toutes les propriétés des propertysource traitées seront affichées, peu importe les prefixes)
 *     - NONE (rien ne sera affiché)
 *     - PREFIX (valeur par défaut) : affiche les propriétés dont le prefixe est dans fr.insee.properties.log.key.prefixes
 *     et qui ne font pas partie des propertySource ignorés (le nom des propertysource ignorés figurent
 *     dans fr.insee.properties.log.sources.ignored))
 *   - cf enum PropertySelectorEnum
 * - fr.insee.properties.log.sources.ignored : nom des propertysource (ex: application-dev.properties) qui seront ignorées.
 *   - la valeur attendue est une liste de noms séparés par des virgules. Si vide alors aucune propertysource ne sera ignorée
 *   - Valeur par défaut : PropertiesLogger.propertySourcesIgnoreesParDefaut
 * - fr.insee.properties.log.key.prefixes : permet de définir les préfixes des propriétés qui seront affichées.
 *   - Valeur par défaut : PropertiesLogger.prefixesAffichesParDefaut
 *   - la valeur attendue est une liste de noms séparés par des virgules. Si vide aucune propriété ne sera affichée
 * - fr.insee.properties.log.key.hidden : liste des mots pour lesquels la valeur de la propriété sera masquée (si la clé de
 * la propriété contient l'un des mots de la liste alors la valeur sera masquée).
 *   - Valeur par défaut : PropertiesLogger.motsCachesParDefaut
 *   - la valeur attendue est une liste de noms séparés par des virgules. /!\ ☠️ si vide tous les secrets seront affichés dans la log
 *
 */
@Slf4j
public class PropertiesLogger implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    public static final String PROPERTY_KEY_FOR_PREFIXES = "fr.insee.properties.log.key.prefixes";
    public static final String PROPERTY_KEY_FOR_MORE_HIDDEN = "fr.insee.properties.log.key.hidden.more";
    public static final String PROPERTY_KEY_FOR_SOURCES_IGNORED = "fr.insee.properties.log.sources.ignored";
    public static final String PROPERTY_KEY_FOR_SOURCES_SELECT = "fr.insee.properties.log.key.select";
    private static final Set<String> baseMotsCaches = Set.of("password", "pwd", "jeton", "token", "secret", "credential", "pw");
    private static final Set<String> prefixesAffichesParDefaut= Set.of("fr.insee","logging","keycloak","spring","application","server","springdoc","management");
    private static final Set<String> propertySourcesIgnoreesParDefaut = Set.of("systemProperties", "systemEnvironment");
    public static final PropertySelectorEnum PROPERTY_SELECTOR_PAR_DEFAUT = PropertySelectorEnum.PREFIX;
    private static Set<String> prefixForSelectedProps;

    private final Collection<String> propertySourceNames=new ArrayList<>();
    private Set<String> hiddensProps;
    private Set<String> ignoredPropertySources;
    private PropertySelector propertySelector;

    @Override
    public void onApplicationEvent(@NonNull ApplicationEnvironmentPreparedEvent event) {
        Environment environment=event.getEnvironment();

        var props= new StringBuilder();
        this.hiddensProps = getMoreHiddenPropsFromPropertyAndMerge(environment);
        prefixForSelectedProps = environment.getProperty(PROPERTY_KEY_FOR_PREFIXES, Set.class, prefixesAffichesParDefaut);
        this.ignoredPropertySources = environment.getProperty(PROPERTY_KEY_FOR_SOURCES_IGNORED, Set.class, propertySourcesIgnoreesParDefaut);
        var propertySelectorType=this.getSelectorFromProperty(environment.getProperty(PROPERTY_KEY_FOR_SOURCES_SELECT))
                                     .orElse(PROPERTY_SELECTOR_PAR_DEFAUT);
        log.atDebug().log(()->"Logging "+propertySelectorType.forLogging());
        this.propertySelector=propertySelectorType.propertySelector();

        ((AbstractEnvironment) environment).getPropertySources().stream()
                .filter(this::isEnumerable)
                .filter(this::sourceWillBeProcessed)
                .map(this::rememberPropertySourceNameThenCast)
                .map(EnumerablePropertySource::getPropertyNames)
                .flatMap(Arrays::stream)
                .distinct()
                .filter(Objects::nonNull)
                .filter(this::filterFromPropertySelector)
                .forEach(key-> props.append(key).append(" = ")
                        .append(resoutValeurAvecMasquePwd(key, environment))
                        .append(System.lineSeparator()));
        props.append("============================================================================");
        props.insert(0, """
                ===============================================================================================
                                                Valeurs des properties pour :
                %s                                        
                ===============================================================================================
                """.formatted(this.propertySourceNames.stream().reduce("",(l, e)->l+System.lineSeparator()+"- "+e )));
        log.info(props.toString());

    }

    private static Set<String> getMoreHiddenPropsFromPropertyAndMerge(Environment environment) {
        var moreProps = environment.getProperty(PROPERTY_KEY_FOR_MORE_HIDDEN, Set.class);
        var retour = baseMotsCaches;
        if (moreProps != null){
            retour=new HashSet<>(moreProps);
            retour.addAll(baseMotsCaches);
        }
        return retour;
    }

    private Optional<PropertySelectorEnum> getSelectorFromProperty(String property) {
        if(property!=null){
            try{
                return Optional.of(PropertySelectorEnum.valueOf(property));
            }catch (IllegalArgumentException ie){
                log.atTrace().log(()->"Impossible de convertir "+property+" en une constante de PropertySelectorEnum. Le PropertySelector par défaut sera utilisé.");
            }
        }
        return Optional.empty();
    }

    private boolean filterFromPropertySelector(@NonNull String s) {
        if (! this.propertySelector.filter(s)){
            log.atDebug().log(()->s+ " ne commence pas par un des prefix retenus pour être loguée");
            return false;
        }
        return true;
    }

    private boolean sourceWillBeProcessed(PropertySource<?> propertySource) {

        if (ignoredPropertySources.contains(propertySource.getName())){
            log.atDebug().log(()->propertySource+ " sera ignorée");
            return false;
        }
        return true;
    }

    private EnumerablePropertySource<?> rememberPropertySourceNameThenCast(PropertySource<?> propertySource) {
        this.propertySourceNames.add(propertySource.getName());
        return (EnumerablePropertySource<?>) propertySource;
    }

    private boolean isEnumerable(PropertySource<?> propertySource) {
        if (! (propertySource instanceof EnumerablePropertySource)){
            log.atDebug().log(()->propertySource+ " n'est pas EnumerablePropertySource : impossible à lister");
            return false;
        }
        return true;
    }

    private Object resoutValeurAvecMasquePwd(String key, Environment environment) {
        if (hiddensProps.stream().anyMatch(key::contains)) {
            return "******";
        }
        return environment.getProperty(key);

    }


    @FunctionalInterface
    private interface PropertySelector {
        boolean filter(String s);
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private enum PropertySelectorEnum {
        ALL(s->true, ()->"all properties"),
        NONE(s->false, ()->"no properties"),
        PREFIX(k->prefixForSelectedProps.stream().anyMatch(k::startsWith), () -> "properties starting with "+ prefixForSelectedProps);

        private final PropertySelector propertySelector;
        private final Supplier<String> logString;

        public PropertySelector propertySelector() {
            return propertySelector;
        }

        public String forLogging(){
            return logString.get();
        }

    }
}

