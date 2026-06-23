package fr.insee.queen.application.configuration.properties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Derives {@code application.group.path-singular} and {@code application.group.path-plural}
 * from {@code application.group.kind} and injects them into the {@code Environment}.
 *
 * <p>Why an EnvironmentPostProcessor and not values directly in application.yml ?
 * {@code path-singular} and {@code path-plural} are not independent settings — they are
 * consequences of {@code kind}. Declaring all three in application.yml would create three
 * properties to keep in sync manually. Deriving them here ensures they are always consistent
 * with {@code kind}, with no risk of misconfiguration.
 *
 * <p>Why not a @Bean or @ConfigurationProperties ?
 * Spring resolves {@code @RequestMapping} placeholders (e.g. {@code ${application.group.path-singular}})
 * during context refresh, before most beans are instantiated. The properties must therefore
 * already exist in the {@code Environment} at that point. An {@code EnvironmentPostProcessor},
 * registered via {@code META-INF/spring.factories}, runs early enough to satisfy this constraint.
 */
public class GroupPathEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String KIND_PROPERTY = "application.group.kind";
    private static final String PATH_SINGULAR_PROPERTY = "application.group.path-singular";
    private static final String PATH_PLURAL_PROPERTY = "application.group.path-plural";
    private static final String PROPERTY_SOURCE_NAME = "groupPathDerivedProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String rawKind = environment.getProperty(KIND_PROPERTY);
        GroupKind kind = resolveKind(rawKind);

        Map<String, Object> derived = new HashMap<>();
        derived.put(PATH_SINGULAR_PROPERTY, kind.getPathSingular());
        derived.put(PATH_PLURAL_PROPERTY, kind.getPathPlural());

        // addFirst so these derived properties take precedence over any accidental manual declaration
        environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, derived));
    }

    private GroupKind resolveKind(String rawKind) {
        if (rawKind == null || rawKind.isBlank()) {
            return GroupKind.CAMPAIGN;
        }
        try {
            return GroupKind.valueOf(rawKind.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException _) {
            return GroupKind.CAMPAIGN;
        }
    }
}
