package com.itxiaoqi.userservice.config;

import com.itxiaoqi.userservice.filter.AuthenticationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI-Deepseek
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<AuthenticationFilter> authenticationFilterRegistration(AuthenticationFilter filter) {
        FilterRegistrationBean<AuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        
        registration.addUrlPatterns("/*");
        
        registration.addInitParameter("exclusions",
            "/user/login," +
            "/user/register");
        
        registration.setName("authenticationFilter");
        registration.setOrder(1); // 执行顺序，数字越小优先级越高
        
        return registration;
    }
}