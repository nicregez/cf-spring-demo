/*
 * (c) Copyright 2014 Swisscom AG
 * All Rights Reserved.
 */
package com.swisscom.cloud.demo.spring.app;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
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

/**
 * @author Nicolas Regez
 * @since 24.02.2014
 */
@Configuration
@EnableWebMvc
@EnableJpaRepositories("com.swisscom.cloud.demo.spring.service")
@EnableTransactionManagement
@ComponentScan("com.swisscom.cloud.demo.spring")
@PropertySources({
	@PropertySource("classpath:application.properties")})
public class SpringDemoWebApplication extends WebMvcConfigurerAdapter {

	/**
	 * See https://jira.spring.io/browse/SPR-8539 for an explanation why this has to be here as long as properties are replaced with "${}" notation
	 * and not {@link Environment}.
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

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

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBean(DataSource datasource) {
        LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
        emfb.setDataSource(datasource);
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
	@Bean(name = "spring-demo-db")
	public DataSource dataSource( //
			@Value("${db.driver}") String driver, //
			@Value("${db.url}") String url, //
			@Value("${db.username}") String username, //
			@Value("${db.password}") String password) {

		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName(driver);
		ds.setUrl(url);
		ds.setUsername(username);
		ds.setPassword(password);
		return ds;
	}

}
