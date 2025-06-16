package com.itxiaoqi.userservice.config;

import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * AI-ChatGPT
 */
@Configuration
public class SeataDataSourceProxyConfig {

    @Bean
    public DataSource dataSource(DataSource originalDataSource) {
        return new DataSourceProxy(originalDataSource);
    }
}
