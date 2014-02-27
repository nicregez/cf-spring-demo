/*
 * (c) Copyright 2014 Swisscom AG
 * All Rights Reserved.
 */
package com.swisscom.cloud.demo.spring.app;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Nicolas Regez
 * @since 24.02.2014
 */
@Configuration
@EnableWebMvc
@EnableJpaRepositories
@EnableTransactionManagement
@ComponentScan("com.swisscom.cloud.demo.spring")
@PropertySource("classpath:application.properties")
public class SpringDemoWebApplication extends WebMvcConfigurerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(SpringDemoWebApplication.class);

    @Autowired
    ApplicationContext ctx;

    @Resource
    private Environment env;

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Bean
    public UrlBasedViewResolver viewResolver() {
        UrlBasedViewResolver resolver = new UrlBasedViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(emf);
        return tm;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean() {
        LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
        emfb.setDataSource(ctx.getBean("datasource", DataSource.class));
        emfb.setPackagesToScan(new String[] {"com.swisscom.cloud.demo.spring.model"});
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setGenerateDdl(true);
        adapter.setDatabase(Database.MYSQL);
        adapter.setShowSql(true);
        emfb.setJpaVendorAdapter(adapter);
        emfb.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        emfb.setPersistenceUnitName("default");
        return emfb;
    }

    /**
     * @return Access to relational database system for local deployments
     */
    @Bean(name="datasource")
    @Profile(value = {"default"})
    public DataSource dataSourceDefault() {
        logger.info("Default Spring Profile");

        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(env.getRequiredProperty("db.driver"));
        ds.setUrl(env.getRequiredProperty("db.url"));
        ds.setUsername(env.getRequiredProperty("db.username"));
        ds.setPassword(env.getRequiredProperty("db.password"));
        return ds;
    }

    /**
     * WORKAROUND because Spring reconfiguration framework in Cloud Foundry
     * Java Buildpack does not (yet, as-of Feb 2014) support auto-reconfiguration
     * based on annotations declared beans.
     * 
     * The first service bound to the application is selected.
     * If you want to select a different service, for example based on its
     * name, then modify this code according to your needs.
     *
     * @return Access to relational database system for deployments in the cloud
     */
    @Bean(name="datasource")
    @Profile(value = {"cloud"})
    public DataSource dataSourceCloud() {
        logger.info("Cloud Spring Profile");

        // TODO: This is a WORKAROUND, should be the buildpack's duty
        if (System.getenv("VCAP_SERVICES") == null) {
            logger.error("Your app is not running in a Cloud Foundry environment.");
            throw new RuntimeException("Not a CloudFoundry environment. Cannot create DataSource");
        }

        String rawJsonString = System.getenv("VCAP_SERVICES");
        Map<String, Map<String, String>> info = extractConnectionProperties(rawJsonString);
        Map<String, String> entry = info.values().iterator().next();

        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(entry.get("driverClassName"));
        ds.setUrl(entry.get("url"));
        ds.setUsername(entry.get("user"));
        ds.setPassword(entry.get("pass"));
        return ds;
    }

    private static final String PRODUCT = "mysql";
    private static final String DRIVER_CLASSNAME = "com.mysql.jdbc.Driver";

    /**
     * credentials expected for each service instance:
     *
     * - hostname (alternate: host)
     * - port
     * - name (alternate: database)
     * - username
     * - password
     *
     * @param rawJsonString
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String, Map<String, String>> extractConnectionProperties(String rawJsonString) {
        Map<String, List<Map<String, Object>>> rawJsonMap;
        try {
            rawJsonMap = new ObjectMapper().readValue(rawJsonString, Map.class);
        } catch (Exception ex) {
            return null;
        }

        Map<String, Map<String, String>> result = new HashMap<String, Map<String,String>>();
        for (String key : rawJsonMap.keySet()) {
            List<Map<String, Object>> list = rawJsonMap.get(key);
            for (Map<String, Object> element : list) {
                String serviceName = (String) element.get("name");
                Map<String, Object> credentials = (Map<String, Object>) element.get("credentials");

                // host
                String h = (String) credentials.get("hostname");
                if (h == null) {
                    h = (String) credentials.get("host");
                    if (h == null) {
                        logger.error("service-instance [name = {}] UNDEFINED hostname/host", serviceName);
                    }
                }

                // port
                Integer p = (Integer) credentials.get("port");
                if (p == null) {
                    logger.error("service-instance [name = {}] UNDEFINED port", serviceName);
                }

                // name (database name)
                String n = (String) credentials.get("name");
                if (n == null) {
                    n = (String) credentials.get("database");
                    if (n == null) {
                        logger.error("service-instance [name = {}] UNDEFINED database name/database", serviceName);
                    }
                }

                String connUrl = "jdbc:" + PRODUCT + "://" + h + ":" + p + "/" + n;

                // username
                String username = (String) credentials.get("username");
                if (username == null) {
                    logger.error("service-instance [name = {}] username unknown", serviceName);
                }

                //  password
                String password = (String) credentials.get("password");
                if (password == null) {
                    logger.error("service-instance [name = {}] password unknown", serviceName);
                }

                Map<String, String> entry = new HashMap<String, String>();
                entry.put("driverClassName", DRIVER_CLASSNAME);
                entry.put("url", connUrl);
                entry.put("user", username);
                entry.put("pass", password);

                logger.debug("service-instance details [service-name = {}, credentials = {}]", serviceName, entry);
                result.put(serviceName, entry);
            }
        }
        return result;
    }

}
