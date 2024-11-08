package com.animal.java.datasource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.jooq.ExecuteListener;
import org.jooq.SQLDialect;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator;
import org.springframework.boot.autoconfigure.jooq.SpringTransactionProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Slf4j
@EnableTransactionManagement
@EnableConfigurationProperties({
        JooqConfigSettingsProperties.class,
        CommonDataSourceConfiguration.AnimaliaHikari.class
})
public class CommonDataSourceConfiguration {

    public static final String CLASS_PREFIX = "common";
    public static final String DATASOURCE_NAME = CLASS_PREFIX + "commonDatasource";
    public static final String TRANSACTION_MANAGER = CLASS_PREFIX + "TransactionManager";
    public static final String TRANSACTION_PROVIDER = CLASS_PREFIX + "TransactionProvider";
    public static final String CONNECTION_PROVIDER = CLASS_PREFIX + "ConnectionProvider";
    public static final String DSL_CONTEXT = CLASS_PREFIX + "DSLContext";
    public static final String EXECUTE_LISTENER_PROVIDER = CLASS_PREFIX + "ExecuteListenerProvider";
    public static final String SQL_STATISTICS_LISTENER = CLASS_PREFIX + "SqlStatisticsListener";
    public static final String JOOQ_DEFAULT_CONFIGURATION = CLASS_PREFIX + "JooqDefaultConfiguration";



    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    public static class AnimaliaHikari extends HikariConfig {
        // do nothing
    }

    @Bean(name=DATASOURCE_NAME)
    public DataSource dataSource(DataSourceProperties properties, AnimaliaHikari hikari){
        hikari.setJdbcUrl(properties.determineUrl());
        hikari.setDriverClassName(properties.determineDriverClassName());
        hikari.setUsername(properties.determineUsername());
        hikari.setPassword(properties.determinePassword());
        hikari.addDataSourceProperty("cachePrepStmts", true);
        hikari.addDataSourceProperty("prepStmtCacheSize", 250);
        hikari.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        hikari.addDataSourceProperty("userServerPrepStmts", true);
        hikari.setAutoCommit(false);

        return new HikariDataSource(hikari);
    }

    @Bean(name=TRANSACTION_MANAGER)
    public PlatformTransactionManager transactionManager(@Qualifier(DATASOURCE_NAME) DataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean(name=TRANSACTION_PROVIDER)
    public SpringTransactionProvider transactionProvider(@Qualifier(TRANSACTION_MANAGER) PlatformTransactionManager transactionManager){
        return new SpringTransactionProvider(transactionManager);
    }

    @Bean(name = CONNECTION_PROVIDER)
    public DataSourceConnectionProvider connectionProvider(@Qualifier(DATASOURCE_NAME) DataSource dataSource){
        return new DataSourceConnectionProvider(new TransactionAwareDataSourceProxy(dataSource));
    }

    @Bean(name = EXECUTE_LISTENER_PROVIDER)
    public DefaultExecuteListenerProvider defaultExecuteListenerProvider(){
        return new DefaultExecuteListenerProvider(new JooqExceptionTranslator());
    }

    @Bean(name = DSL_CONTEXT)
    public DefaultDSLContext dsl(@Qualifier(JOOQ_DEFAULT_CONFIGURATION)DefaultConfiguration configuration){
        return new DefaultDSLContext(configuration);
    }

    @Bean(name = SQL_STATISTICS_LISTENER)
    public SqlStatisticsListener sqlStatisticsListener(){
        return  new SqlStatisticsListener();
    }

    @Bean(name = JOOQ_DEFAULT_CONFIGURATION)
    public DefaultConfiguration configuration(
            @Qualifier(DATASOURCE_NAME) DataSource dataSource,
            @Qualifier(TRANSACTION_PROVIDER) SpringTransactionProvider transactionProvider,
            @Qualifier(CONNECTION_PROVIDER) DataSourceConnectionProvider connectionProvider,
            @Qualifier(EXECUTE_LISTENER_PROVIDER) DefaultExecuteListenerProvider listenerProvider,
            @Qualifier(SQL_STATISTICS_LISTENER) ExecuteListener executeListener,
            JooqConfigSettingsProperties properties){

        DefaultConfiguration jooqConfiguration = new DefaultConfiguration();
        jooqConfiguration.set(connectionProvider);
        jooqConfiguration.set(listenerProvider);
        jooqConfiguration.set(transactionProvider);
        jooqConfiguration.set(SQLDialect.POSTGRES);

        jooqConfiguration.settings().setReturnRecordToPojo(properties.isReturnRecordToPojo());

        if(properties.isLogStatistics()){
            jooqConfiguration.setExecuteListenerProvider(new DefaultExecuteListenerProvider(executeListener));
        }

        log.info("Jooq Configuration default settings: {}", properties);

        return jooqConfiguration;
    }
}
