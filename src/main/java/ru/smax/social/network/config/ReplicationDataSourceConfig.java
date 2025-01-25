package ru.smax.social.network.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ReplicationDataSourceConfig {

    @Primary
    @Bean
    @DependsOn({"writeDataSource", "read1DataSource", "routingDataSource"})
    public DataSource dataSource() {
        return new LazyConnectionDataSourceProxy(routingDataSource());
    }

    @Bean
    public DataSource routingDataSource() {
        var readOnlyReplicas = List.of("read1", "read2");

        ReplicationRoutingDataSource routingDataSource = new ReplicationRoutingDataSource(readOnlyReplicas);

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("write", writeDataSource());
        dataSourceMap.put("read1", read1DataSource());
        dataSourceMap.put("read2", read2DataSource());
        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(writeDataSource());

        return routingDataSource;
    }

    @Bean
    public DataSource writeDataSource() {
        return createHikariDataSource(5432);
    }

    @Bean
    public DataSource read1DataSource() {
        return createHikariDataSource(25432);
    }

    @Bean
    public DataSource read2DataSource() {
        return createHikariDataSource(35432);
    }

    private static DataSource createHikariDataSource(int port) {
        var config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:%d/social_network".formatted(port));
        config.setUsername("postgres");
        config.setPassword("password");
        config.setDriverClassName("org.postgresql.Driver");
        return new HikariDataSource(config);
    }
}
