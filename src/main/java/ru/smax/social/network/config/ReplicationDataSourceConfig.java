package ru.smax.social.network.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReplicationDataSourceConfig {

    @Primary
    @Bean
    public DataSource dataSource(DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    @Bean
    public DataSource routingDataSource(@Value("${DB_HOST}") String host) {
        ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("write", createHikariDataSource(host, 5432));
        dataSourceMap.put("read", createHikariDataSource(host, 5433));

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(dataSourceMap.get("write"));
        return routingDataSource;
    }

    private static DataSource createHikariDataSource(String host, int port) {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://%s:%d/social_network".formatted(host, port));
        config.setUsername("postgres");
        config.setPassword("password");
        config.setDriverClassName("org.postgresql.Driver");

        System.out.println("WOLOLO: created JDBC url\n" + config.getJdbcUrl());

        return new HikariDataSource(config);
    }
}
