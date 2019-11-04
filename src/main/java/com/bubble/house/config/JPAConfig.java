package com.bubble.house.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * JPA配置：Hibernate + MySQL
 *
 * @author wugang
 * date: 2019-11-01 14:36
 **/
@Configuration
@EnableJpaRepositories(basePackages = "com.bubble.house.repository")
//// 允许事务管理
@EnableTransactionManagement
public class JPAConfig {

//    @Value(value = "${spring.datasource.driver-class-name}")
//    private String driverClassName;
//    @Value(value = "${spring.datasource.url}")
//    private String url;
//    @Value(value = "${spring.datasource.username}")
//    private String userName;
//    @Value(value = "${spring.datasource.password}")
//    private String password;
//
//    /**
//     * 建立数据源
//     */
//    @Bean
//    public DataSource dataSource() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(driverClassName);
//        dataSource.setUrl(url);
//        dataSource.setUsername(userName);
//        dataSource.setPassword(password);
//        return dataSource;
//    }

    /**
     * 建立数据源
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 实体类的管理工厂
     */
    @Bean
    public EntityManagerFactory entityManagerFactory() {
        // 实例化jpa适配器，由于使用的是hibernate所以用HibernateJpaVendorAdapter
        HibernateJpaVendorAdapter japVendor = new HibernateJpaVendorAdapter();
        // 不生成sql
        japVendor.setGenerateDdl(false);
        japVendor.setDatabase(Database.MYSQL);

        // 实例化管理工厂
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setJpaVendorAdapter(japVendor);
        // 设置扫描包名
        entityManagerFactory.setPackagesToScan("com.bubble.house.entity");
        entityManagerFactory.afterPropertiesSet();
        return entityManagerFactory.getObject();
    }

    /**
     * 事务管理
     */
    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }


}
