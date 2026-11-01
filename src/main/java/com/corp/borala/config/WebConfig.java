package com.corp.borala.config;

import com.corp.borala.security.AcessoRestauranteInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AcessoRestauranteInterceptor acessoRestauranteInterceptor;

    public WebConfig(AcessoRestauranteInterceptor acessoRestauranteInterceptor) {
        this.acessoRestauranteInterceptor = acessoRestauranteInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(acessoRestauranteInterceptor).addPathPatterns("/restaurantes/**")
                .excludePathPatterns("/restaurantes/*/ver");
    }
}
