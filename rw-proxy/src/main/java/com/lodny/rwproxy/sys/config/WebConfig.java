package com.lodny.rwproxy.sys.config;

import com.lodny.rwcommon.filter.JwtFilter;
import com.lodny.rwcommon.interceptor.JwtTokenInterceptor;
import com.lodny.rwcommon.resolver.LoginUserMethodArgumentResolver;
import com.lodny.rwproxy.sys.filter.JwtFilter4DB;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtTokenInterceptor jwtTokenInterceptor;
    private final LoginUserMethodArgumentResolver loginUserMethodArgumentResolver;
    private final JwtFilter jwtFilter;
    private final JwtFilter4DB jwtFilter4DB;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtTokenInterceptor)
                .addPathPatterns("/api/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserMethodArgumentResolver);
    }

    @Bean
    public FilterRegistrationBean<JwtFilter> addJwtFilter() {
        FilterRegistrationBean<JwtFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(jwtFilter);
        registrationBean.addUrlPatterns("/api/*");

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<JwtFilter4DB> addJwtFilter4DB() {
        FilterRegistrationBean<JwtFilter4DB> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(jwtFilter4DB);
        registrationBean.addUrlPatterns("/api/*");

        return registrationBean;
    }
}
