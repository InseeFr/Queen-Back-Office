package fr.insee.queen.api.configuration;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import fr.insee.queen.api.helper.ApiJpaRepositoryFactoryBean;
import liquibase.integration.spring.SpringLiquibase;

@Configuration
@ConditionalOnProperty(prefix = "fr.insee.queen.application", name = "persistenceType", havingValue = "JPA", matchIfMissing = true)
@EnableJpaRepositories(
        repositoryFactoryBeanClass = ApiJpaRepositoryFactoryBean.class,
        entityManagerFactoryRef = "entityManagerFactory",
        basePackages = "fr.insee.queen.api.repository")
public class ApiJpaRepositoryConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String dialect;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;
    
    @Value("${environment}")
    private String environment;

    @Value("${fr.insee.queen.persistence.database.driver}")
    private String jdbcDriver;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiJpaRepositoryConfig.class);

    public ApiJpaRepositoryConfig() {
        LOGGER.info("Repository Configuration: {}", ApiJpaRepositoryConfig.class);
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("dataSource") DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("fr.insee.queen.api.domain");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(properties());

        return em;
    }

    @Bean(name = "dataSource")
    public DataSource dataSource() {
    	try {
    		Class.forName(jdbcDriver);
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    }
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager platformTransactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Properties properties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", dialect);
        properties.setProperty("hibernate.hbm2ddl.auto", ddlAuto);

        return properties;
    }
    
    @Bean(name="liquibase")
    public SpringLiquibase liquibase() {
      SpringLiquibase liquibase = new SpringLiquibase();
      liquibase.setDataSource(dataSource());
      liquibase.setContexts(environment);
      liquibase.setChangeLog("classpath:db/master.xml");
      liquibase.setShouldRun(true);
      return liquibase;
    }
}
