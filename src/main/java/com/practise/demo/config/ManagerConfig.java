package com.practise.demo.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

/**
 * @author lingwang
 * @date 2021/3/16 20:40
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "managerEntityManagerFactory",
        transactionManagerRef = "managerTransactionManager",
        basePackages = {"com.practise.demo.repository.manager"}
)
public class ManagerConfig {

    @Bean(name = "managerDataSource")
    @ConfigurationProperties(prefix = "manager")
    @Primary
    public DataSource dataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "managerEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean managerEntityManagerFactory(
            EntityManagerFactoryBuilder builder, @Qualifier("managerDataSource") DataSource dataSource) {
        return builder.dataSource(dataSource).packages("com.practise.demo.pojo.manager").persistenceUnit("manager")
                .build();
    }

    @Bean(name = "managerTransactionManager")
    @Primary
    public PlatformTransactionManager managerTransactionManager(
            @Qualifier("managerEntityManagerFactory") EntityManagerFactory managerEntityManagerFactory) {
        return new JpaTransactionManager(managerEntityManagerFactory);
    }
}

