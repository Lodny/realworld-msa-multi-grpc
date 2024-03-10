package com.lodny.rwfavorite.sys.config;

import com.lodny.rwcommon.exception.GlobalExceptionHandler;
import com.lodny.rwcommon.filter.JwtFilter;
import com.lodny.rwcommon.interceptor.JwtTokenInterceptor;
import com.lodny.rwcommon.properties.JwtProperty;
import com.lodny.rwcommon.resolver.LoginUserMethodArgumentResolver;
import com.lodny.rwcommon.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(JwtProperty.class)
@RequiredArgsConstructor
public class CommonConfig {
    private final JwtProperty jwtProperty;

    @Bean
    public JwtUtil getJwtUtil() {
        return new JwtUtil(jwtProperty);
    }

    @Bean
    public JwtFilter getJwtFilter() {
        return new JwtFilter(jwtProperty, getJwtUtil());
    }

    @Bean
    public JwtTokenInterceptor getJwtTokenInterceptor() {
        return new JwtTokenInterceptor();
    }

    @Bean
    public LoginUserMethodArgumentResolver getLoginUserMethodArgumentResolver() {
        return new LoginUserMethodArgumentResolver();
    }

    @Bean
    public GlobalExceptionHandler getGlobalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

    @Bean
    public RestTemplate getRestTemplateBean() {
        return new RestTemplate();
    }
}
