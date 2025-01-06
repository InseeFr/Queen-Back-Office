package fr.insee.queen.infrastructure.db.configuration;

import com.zaxxer.hikari.HikariDataSource;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@ConditionalOnProperty(name = "feature.sensitive-data.enabled", havingValue = "true")
@Component
@RequiredArgsConstructor
public class DBCipheredBeanPostProcessor implements BeanPostProcessor {

    private final CipherProperties cipherProperties;

    @Override
    @NonNull
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof LiquibaseProperties liquibaseProperties) {
            List<String> contexts = liquibaseProperties.getContexts();
            if (contexts == null) {
                contexts = new ArrayList<>();
                liquibaseProperties.setContexts(contexts);
            }
            contexts.add("ciphered-data");
        }

        if (bean instanceof HikariDataSource dataSource) {
            String initSql = String.format(
                    "select set_config('data.encryption.key', '%s', false)",
                    cipherProperties.getEncryptionSecretKey()
            );
            dataSource.setConnectionInitSql(initSql);
        }
        return bean;
    }

    @Override
    @NonNull
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        return bean;
    }
}