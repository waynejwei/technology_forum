package com.example.technology_forum.configuration;

import com.example.technology_forum.interceptor.sessionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 1200)
public class sessionConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 设置拦截的路径、不拦截的路径、优先级等等
        registry.addInterceptor(new sessionInterceptor()).addPathPatterns("/*").excludePathPatterns("/login").excludePathPatterns("/register");
    }

}

