package com.guducat.backend.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 配置类
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册 Sa-Token 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 需要登录认证的路由
            SaRouter.match("/user/getInfo", r -> StpUtil.checkLogin());
            SaRouter.match("/user/updateInfo", r -> StpUtil.checkLogin());
            SaRouter.match("/user/isAdmin", r -> StpUtil.checkLogin());
            SaRouter.match("/user/deleteAccount", r -> StpUtil.checkLogin());

            // 管理员接口
            SaRouter.match("/admin/**", r -> {
                StpUtil.checkLogin();
                StpUtil.checkRole("admin");
            });
        })).addPathPatterns("/**");
    }
}
