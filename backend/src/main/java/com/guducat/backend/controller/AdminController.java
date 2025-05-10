package com.guducat.backend.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.guducat.backend.entity.User;
import com.guducat.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户
     */
    @SaCheckRole("admin")
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.list();
    }

    /**
     * 获取单个用户
     */
    @SaCheckRole("admin")
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getById(id);
    }

    /**
     * 删除用户
     */
    @SaCheckRole("admin")
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.removeById(id);
    }
}