package com.guducat.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {

    /**
     * 跨域过滤器
     * 注意：这里添加了 @Order(Ordered.HIGHEST_PRECEDENCE) 注解
     * 确保 CORS 过滤器在所有其他过滤器之前执行，包括 SaToken 过滤器
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 允许跨域的头部信息
        config.addAllowedHeader("*");
        // 允许跨域的方法
        config.addAllowedMethod("*");
        // 允许跨域的源
        config.addAllowedOriginPattern("*"); // 允许所有源
        // 允许携带cookie信息
        config.setAllowCredentials(true);
        // 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再次预检
        config.setMaxAge(3600L);
        // 添加暴露的头部信息
        config.addExposedHeader("satoken");
        config.addExposedHeader("Content-Type");
        config.addExposedHeader("X-Requested-With");
        config.addExposedHeader("accept");
        config.addExposedHeader("Origin");
        config.addExposedHeader("Access-Control-Request-Method");
        config.addExposedHeader("Access-Control-Request-Headers");

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
