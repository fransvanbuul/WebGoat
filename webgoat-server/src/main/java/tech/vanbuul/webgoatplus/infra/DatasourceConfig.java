package tech.vanbuul.webgoatplus.infra;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
public class DatasourceConfig {

    @Bean
    @Qualifier("plus")
    @ConfigurationProperties(prefix = "plus.datasource")
    public DataSource plusDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Qualifier("plus")
    @ConfigurationProperties(prefix = "plus.flyway")
    public Flyway eventsFlyway(@Qualifier("plus") DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        return flyway;
    }

    @Bean
    @Qualifier("plus")
    public FlywayMigrationInitializer plusFlywayMigrationInitializer(
            @Qualifier("plus") Flyway flyway) {
        return new FlywayMigrationInitializer(flyway);
    }

    @Bean
    @Qualifier("plus")
    public DataSourceTransactionManager plusDataSourceTransactionManager(
            @Qualifier("plus") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @Qualifier("plus")
    public TransactionTemplate plusDataSourceTransactionTemplate(
            @Qualifier("plus") DataSourceTransactionManager plusDataSourceTransactionManager) {
        return new TransactionTemplate(plusDataSourceTransactionManager);
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    @Qualifier("plus")
    public JdbcTemplate plusJdbcTemplate(
            @Qualifier("plus") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }


}
