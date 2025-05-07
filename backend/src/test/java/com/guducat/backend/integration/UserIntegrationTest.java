package com.guducat.backend.integration;

import cn.dev33.satoken.util.SaResult;
import com.guducat.backend.controller.UserController;
import com.guducat.backend.entity.User;
import com.guducat.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户功能集成测试
 * 
 * 注意：这个测试类使用了 @Transactional 注解，所以测试完成后会自动回滚事务，不会影响数据库
 */
@SpringBootTest
@ActiveProfiles("test") // 使用测试环境配置
@Transactional
public class UserIntegrationTest {

    @Autowired
    private UserController userController;
    
    @Autowired
    private UserService userService;
    
    @Test
    void testRegisterAndLoginFlow() {
        // 1. 注册新用户
        String username = "integrationtest";
        String nickname = "Integration Test";
        String email = "integration@test.com";
        String avatarUrl = "https://example.com/avatar.jpg";
        String password = "testpassword";
        
        SaResult registerResult = userController.doRegister(
            username, nickname, email, avatarUrl, password, password
        );
        
        // 验证注册成功
        assertEquals(200, registerResult.getCode());
        assertNotNull(registerResult.getData());
        
        // 2. 使用用户名登录
        SaResult loginWithUsernameResult = userController.doLogin(username, password);
        
        // 验证用户名登录成功
        assertEquals(200, loginWithUsernameResult.getCode());
        assertNotNull(loginWithUsernameResult.getData());
        
        // 3. 使用邮箱登录
        SaResult loginWithEmailResult = userController.doLogin(email, password);
        
        // 验证邮箱登录成功
        assertEquals(200, loginWithEmailResult.getCode());
        assertNotNull(loginWithEmailResult.getData());
        
        // 4. 获取用户信息
        SaResult userInfoResult = userController.getInfo();
        
        // 验证获取用户信息成功
        assertEquals(200, userInfoResult.getCode());
        User userInfo = (User) userInfoResult.getData();
        assertEquals(username, userInfo.getUsername());
        assertEquals(nickname, userInfo.getNickname());
        assertEquals(email, userInfo.getEmail());
        assertEquals(avatarUrl, userInfo.getAvatarUrl());
        assertNull(userInfo.getPassword()); // 确保密码被清除
        
        // 5. 更新用户信息
        String newNickname = "Updated Integration Test";
        String newEmail = "updated.integration@test.com";
        String newAvatarUrl = "https://example.com/updated-avatar.jpg";
        
        SaResult updateResult = userController.updateInfo(newNickname, newEmail, newAvatarUrl);
        
        // 验证更新成功
        assertEquals(200, updateResult.getCode());
        
        // 6. 再次获取用户信息，验证更新是否生效
        SaResult updatedUserInfoResult = userController.getInfo();
        
        // 验证获取更新后的用户信息成功
        assertEquals(200, updatedUserInfoResult.getCode());
        User updatedUserInfo = (User) updatedUserInfoResult.getData();
        assertEquals(username, updatedUserInfo.getUsername()); // 用户名不变
        assertEquals(newNickname, updatedUserInfo.getNickname());
        assertEquals(newEmail, updatedUserInfo.getEmail());
        assertEquals(newAvatarUrl, updatedUserInfo.getAvatarUrl());
    }
    
    @Test
    void testRegisterWithExistingUsername() {
        // 1. 先注册一个用户
        String username = "existinguser";
        String nickname = "Existing User";
        String email = "existing@test.com";
        String avatarUrl = "https://example.com/avatar.jpg";
        String password = "testpassword";
        
        SaResult firstRegisterResult = userController.doRegister(
            username, nickname, email, avatarUrl, password, password
        );
        
        // 验证第一次注册成功
        assertEquals(200, firstRegisterResult.getCode());
        
        // 2. 尝试使用相同的用户名再次注册
        String newEmail = "another@test.com";
        
        SaResult secondRegisterResult = userController.doRegister(
            username, nickname, newEmail, avatarUrl, password, password
        );
        
        // 验证第二次注册失败
        assertEquals(500, secondRegisterResult.getCode());
        assertTrue(secondRegisterResult.getMsg().contains("注册失败"));
    }
    
    @Test
    void testRegisterWithExistingEmail() {
        // 1. 先注册一个用户
        String username = "firstuser";
        String nickname = "First User";
        String email = "duplicate@test.com";
        String avatarUrl = "https://example.com/avatar.jpg";
        String password = "testpassword";
        
        SaResult firstRegisterResult = userController.doRegister(
            username, nickname, email, avatarUrl, password, password
        );
        
        // 验证第一次注册成功
        assertEquals(200, firstRegisterResult.getCode());
        
        // 2. 尝试使用相同的邮箱再次注册
        String newUsername = "seconduser";
        
        SaResult secondRegisterResult = userController.doRegister(
            newUsername, nickname, email, avatarUrl, password, password
        );
        
        // 验证第二次注册失败
        assertEquals(500, secondRegisterResult.getCode());
        assertTrue(secondRegisterResult.getMsg().contains("注册失败"));
    }
    
    @Test
    void testLoginWithWrongPassword() {
        // 1. 先注册一个用户
        String username = "logintest";
        String nickname = "Login Test";
        String email = "login@test.com";
        String avatarUrl = "https://example.com/avatar.jpg";
        String password = "correctpassword";
        
        SaResult registerResult = userController.doRegister(
            username, nickname, email, avatarUrl, password, password
        );
        
        // 验证注册成功
        assertEquals(200, registerResult.getCode());
        
        // 2. 尝试使用错误的密码登录
        String wrongPassword = "wrongpassword";
        
        SaResult loginResult = userController.doLogin(username, wrongPassword);
        
        // 验证登录失败
        assertEquals(500, loginResult.getCode());
        assertTrue(loginResult.getMsg().contains("登录失败"));
    }
}
