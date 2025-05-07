package com.guducat.backend.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.guducat.backend.entity.User;
import com.guducat.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDoLogin_Success() {
        // 准备测试数据
        String username = "testuser";
        String password = "password123";
        SaTokenInfo mockTokenInfo = new SaTokenInfo();
        mockTokenInfo.setTokenName("satoken");
        mockTokenInfo.setTokenValue("test-token-value");
        
        // 设置模拟行为
        when(userService.login(username, password)).thenReturn(true);
        
        try (MockedStatic<StpUtil> stpUtilMockedStatic = mockStatic(StpUtil.class)) {
            stpUtilMockedStatic.when(StpUtil::getTokenInfo).thenReturn(mockTokenInfo);
            
            // 执行测试
            SaResult result = userController.doLogin(username, password);
            
            // 验证结果
            assertEquals(200, result.getCode());
            assertEquals(mockTokenInfo, result.getData());
            verify(userService, times(1)).login(username, password);
        }
    }
    
    @Test
    void testDoLogin_Failure() {
        // 准备测试数据
        String username = "testuser";
        String password = "wrongpassword";
        
        // 设置模拟行为
        when(userService.login(username, password)).thenReturn(false);
        
        // 执行测试
        SaResult result = userController.doLogin(username, password);
        
        // 验证结果
        assertEquals(500, result.getCode());
        assertEquals("登录失败，请检查用户名或密码", result.getMsg());
        verify(userService, times(1)).login(username, password);
    }
    
    @Test
    void testDoRegister_Success() {
        // 准备测试数据
        String username = "newuser";
        String nickname = "New User";
        String email = "newuser@example.com";
        String avatarUrl = "https://example.com/avatar.jpg";
        String password = "password123";
        String confirmPassword = "password123";
        SaTokenInfo mockTokenInfo = new SaTokenInfo();
        mockTokenInfo.setTokenName("satoken");
        mockTokenInfo.setTokenValue("test-token-value");
        
        // 设置模拟行为
        when(userService.register(username, nickname, email, avatarUrl, password)).thenReturn(true);
        when(userService.login(username, password)).thenReturn(true);
        
        try (MockedStatic<StpUtil> stpUtilMockedStatic = mockStatic(StpUtil.class)) {
            stpUtilMockedStatic.when(StpUtil::getTokenInfo).thenReturn(mockTokenInfo);
            
            // 执行测试
            SaResult result = userController.doRegister(username, nickname, email, avatarUrl, password, confirmPassword);
            
            // 验证结果
            assertEquals(200, result.getCode());
            assertEquals(mockTokenInfo, result.getData());
            verify(userService, times(1)).register(username, nickname, email, avatarUrl, password);
            verify(userService, times(1)).login(username, password);
        }
    }
    
    @Test
    void testDoRegister_PasswordMismatch() {
        // 准备测试数据
        String username = "newuser";
        String nickname = "New User";
        String email = "newuser@example.com";
        String avatarUrl = "https://example.com/avatar.jpg";
        String password = "password123";
        String confirmPassword = "differentpassword";
        
        // 执行测试
        SaResult result = userController.doRegister(username, nickname, email, avatarUrl, password, confirmPassword);
        
        // 验证结果
        assertEquals(500, result.getCode());
        assertEquals("两次输入的密码不一致", result.getMsg());
        verify(userService, never()).register(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(userService, never()).login(anyString(), anyString());
    }
    
    @Test
    void testDoRegister_RegistrationFailure() {
        // 准备测试数据
        String username = "existinguser";
        String nickname = "Existing User";
        String email = "existing@example.com";
        String avatarUrl = "https://example.com/avatar.jpg";
        String password = "password123";
        String confirmPassword = "password123";
        
        // 设置模拟行为
        when(userService.register(username, nickname, email, avatarUrl, password)).thenReturn(false);
        
        // 执行测试
        SaResult result = userController.doRegister(username, nickname, email, avatarUrl, password, confirmPassword);
        
        // 验证结果
        assertEquals(500, result.getCode());
        assertEquals("注册失败，用户名或邮箱可能已存在", result.getMsg());
        verify(userService, times(1)).register(username, nickname, email, avatarUrl, password);
        verify(userService, never()).login(anyString(), anyString());
    }
    
    @Test
    void testGetInfo_Success() {
        // 准备测试数据
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername("testuser");
        mockUser.setNickname("Test User");
        mockUser.setEmail("test@example.com");
        mockUser.setAvatarUrl("https://example.com/avatar.jpg");
        mockUser.setPassword("hashedpassword");
        
        // 设置模拟行为
        try (MockedStatic<StpUtil> stpUtilMockedStatic = mockStatic(StpUtil.class)) {
            stpUtilMockedStatic.when(() -> StpUtil.getLoginId(-1)).thenReturn(userId);
            when(userService.getUserById(userId)).thenReturn(mockUser);
            
            // 执行测试
            SaResult result = userController.getInfo();
            
            // 验证结果
            assertEquals(200, result.getCode());
            User returnedUser = (User) result.getData();
            assertEquals(userId, returnedUser.getId());
            assertEquals("testuser", returnedUser.getUsername());
            assertEquals("Test User", returnedUser.getNickname());
            assertEquals("test@example.com", returnedUser.getEmail());
            assertEquals("https://example.com/avatar.jpg", returnedUser.getAvatarUrl());
            assertNull(returnedUser.getPassword()); // 确保密码被清除
            verify(userService, times(1)).getUserById(userId);
        }
    }
    
    @Test
    void testGetInfo_NotLoggedIn() {
        // 设置模拟行为
        try (MockedStatic<StpUtil> stpUtilMockedStatic = mockStatic(StpUtil.class)) {
            stpUtilMockedStatic.when(() -> StpUtil.getLoginId(-1)).thenReturn(-1);
            
            // 执行测试
            SaResult result = userController.getInfo();
            
            // 验证结果
            assertEquals(500, result.getCode());
            assertEquals("用户未登录", result.getMsg());
            verify(userService, never()).getUserById(anyLong());
        }
    }
    
    @Test
    void testGetInfo_UserNotFound() {
        // 准备测试数据
        Long userId = 999L;
        
        // 设置模拟行为
        try (MockedStatic<StpUtil> stpUtilMockedStatic = mockStatic(StpUtil.class)) {
            stpUtilMockedStatic.when(() -> StpUtil.getLoginId(-1)).thenReturn(userId);
            when(userService.getUserById(userId)).thenReturn(null);
            
            // 执行测试
            SaResult result = userController.getInfo();
            
            // 验证结果
            assertEquals(500, result.getCode());
            assertEquals("用户信息不存在", result.getMsg());
            verify(userService, times(1)).getUserById(userId);
        }
    }
    
    @Test
    void testUpdateInfo_Success() {
        // 准备测试数据
        Long userId = 1L;
        String nickname = "Updated Nickname";
        String email = "updated@example.com";
        String avatarUrl = "https://example.com/updated-avatar.jpg";
        
        // 设置模拟行为
        try (MockedStatic<StpUtil> stpUtilMockedStatic = mockStatic(StpUtil.class)) {
            stpUtilMockedStatic.when(() -> StpUtil.getLoginId(-1)).thenReturn(userId);
            when(userService.updateUserInfo(userId, nickname, email, avatarUrl)).thenReturn(true);
            
            // 执行测试
            SaResult result = userController.updateInfo(nickname, email, avatarUrl);
            
            // 验证结果
            assertEquals(200, result.getCode());
            assertEquals("用户信息更新成功", result.getMsg());
            verify(userService, times(1)).updateUserInfo(userId, nickname, email, avatarUrl);
        }
    }
    
    @Test
    void testUpdateInfo_NotLoggedIn() {
        // 准备测试数据
        String nickname = "Updated Nickname";
        String email = "updated@example.com";
        String avatarUrl = "https://example.com/updated-avatar.jpg";
        
        // 设置模拟行为
        try (MockedStatic<StpUtil> stpUtilMockedStatic = mockStatic(StpUtil.class)) {
            stpUtilMockedStatic.when(() -> StpUtil.getLoginId(-1)).thenReturn(-1);
            
            // 执行测试
            SaResult result = userController.updateInfo(nickname, email, avatarUrl);
            
            // 验证结果
            assertEquals(500, result.getCode());
            assertEquals("用户未登录", result.getMsg());
            verify(userService, never()).updateUserInfo(anyLong(), anyString(), anyString(), anyString());
        }
    }
    
    @Test
    void testUpdateInfo_UpdateFailure() {
        // 准备测试数据
        Long userId = 1L;
        String nickname = "Updated Nickname";
        String email = "existing@example.com"; // 假设这个邮箱已被使用
        String avatarUrl = "https://example.com/updated-avatar.jpg";
        
        // 设置模拟行为
        try (MockedStatic<StpUtil> stpUtilMockedStatic = mockStatic(StpUtil.class)) {
            stpUtilMockedStatic.when(() -> StpUtil.getLoginId(-1)).thenReturn(userId);
            when(userService.updateUserInfo(userId, nickname, email, avatarUrl)).thenReturn(false);
            
            // 执行测试
            SaResult result = userController.updateInfo(nickname, email, avatarUrl);
            
            // 验证结果
            assertEquals(500, result.getCode());
            assertEquals("用户信息更新失败，邮箱可能已被使用", result.getMsg());
            verify(userService, times(1)).updateUserInfo(userId, nickname, email, avatarUrl);
        }
    }
}
