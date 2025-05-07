package com.guducat.backend.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.guducat.backend.entity.User;
import com.guducat.backend.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginWithUsername_Success() {
        // 准备测试数据
        String username = "testuser";
        String password = "password123";
        String hashedPassword = SaSecureUtil.sha256(password);
        
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setPassword(hashedPassword);
        
        // 设置模拟行为
        when(userMapper.findByUsername(username)).thenReturn(mockUser);
        
        // 执行测试
        boolean result = userService.login(username, password);
        
        // 验证结果
        assertTrue(result);
        verify(userMapper, times(1)).findByUsername(username);
        verify(userMapper, never()).findByEmail(anyString());
    }
    
    @Test
    void testLoginWithEmail_Success() {
        // 准备测试数据
        String email = "test@example.com";
        String password = "password123";
        String hashedPassword = SaSecureUtil.sha256(password);
        
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail(email);
        mockUser.setPassword(hashedPassword);
        
        // 设置模拟行为
        when(userMapper.findByUsername(email)).thenReturn(null);
        when(userMapper.findByEmail(email)).thenReturn(mockUser);
        
        // 执行测试
        boolean result = userService.login(email, password);
        
        // 验证结果
        assertTrue(result);
        verify(userMapper, times(1)).findByUsername(email);
        verify(userMapper, times(1)).findByEmail(email);
    }
    
    @Test
    void testLogin_WrongPassword() {
        // 准备测试数据
        String username = "testuser";
        String password = "password123";
        String wrongPassword = "wrongpassword";
        String hashedPassword = SaSecureUtil.sha256(password);
        
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername(username);
        mockUser.setPassword(hashedPassword);
        
        // 设置模拟行为
        when(userMapper.findByUsername(username)).thenReturn(mockUser);
        
        // 执行测试
        boolean result = userService.login(username, wrongPassword);
        
        // 验证结果
        assertFalse(result);
        verify(userMapper, times(1)).findByUsername(username);
    }
    
    @Test
    void testLogin_UserNotFound() {
        // 准备测试数据
        String username = "nonexistentuser";
        String password = "password123";
        
        // 设置模拟行为
        when(userMapper.findByUsername(username)).thenReturn(null);
        when(userMapper.findByEmail(username)).thenReturn(null);
        
        // 执行测试
        boolean result = userService.login(username, password);
        
        // 验证结果
        assertFalse(result);
        verify(userMapper, times(1)).findByUsername(username);
        verify(userMapper, times(1)).findByEmail(username);
    }
    
    @Test
    void testRegister_Success() {
        // 准备测试数据
        String username = "newuser";
        String nickname = "New User";
        String email = "newuser@example.com";
        String avatarUrl = "https://example.com/avatar.jpg";
        String password = "password123";
        
        // 设置模拟行为
        when(userMapper.findByUsername(username)).thenReturn(null);
        when(userMapper.checkEmailExists(email)).thenReturn(0);
        when(userService.save(any(User.class))).thenReturn(true);
        
        // 执行测试
        boolean result = userService.register(username, nickname, email, avatarUrl, password);
        
        // 验证结果
        assertTrue(result);
        verify(userMapper, times(1)).findByUsername(username);
        verify(userMapper, times(1)).checkEmailExists(email);
        verify(userService, times(1)).save(any(User.class));
    }
    
    @Test
    void testRegister_UsernameExists() {
        // 准备测试数据
        String username = "existinguser";
        String nickname = "Existing User";
        String email = "newuser@example.com";
        String avatarUrl = "https://example.com/avatar.jpg";
        String password = "password123";
        
        User existingUser = new User();
        existingUser.setUsername(username);
        
        // 设置模拟行为
        when(userMapper.findByUsername(username)).thenReturn(existingUser);
        
        // 执行测试
        boolean result = userService.register(username, nickname, email, avatarUrl, password);
        
        // 验证结果
        assertFalse(result);
        verify(userMapper, times(1)).findByUsername(username);
        verify(userMapper, never()).checkEmailExists(anyString());
        verify(userService, never()).save(any(User.class));
    }
    
    @Test
    void testRegister_EmailExists() {
        // 准备测试数据
        String username = "newuser";
        String nickname = "New User";
        String email = "existing@example.com";
        String avatarUrl = "https://example.com/avatar.jpg";
        String password = "password123";
        
        // 设置模拟行为
        when(userMapper.findByUsername(username)).thenReturn(null);
        when(userMapper.checkEmailExists(email)).thenReturn(1);
        
        // 执行测试
        boolean result = userService.register(username, nickname, email, avatarUrl, password);
        
        // 验证结果
        assertFalse(result);
        verify(userMapper, times(1)).findByUsername(username);
        verify(userMapper, times(1)).checkEmailExists(email);
        verify(userService, never()).save(any(User.class));
    }
    
    @Test
    void testUpdateUserInfo_Success() {
        // 准备测试数据
        Long userId = 1L;
        String newNickname = "Updated Nickname";
        String newEmail = "newemail@example.com";
        String newAvatarUrl = "https://example.com/new-avatar.jpg";
        
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setNickname("Old Nickname");
        existingUser.setEmail("old@example.com");
        existingUser.setAvatarUrl("https://example.com/old-avatar.jpg");
        
        // 设置模拟行为
        when(userService.getById(userId)).thenReturn(existingUser);
        when(userMapper.checkEmailExists(newEmail)).thenReturn(0);
        when(userService.updateById(any(User.class))).thenReturn(true);
        
        // 执行测试
        boolean result = userService.updateUserInfo(userId, newNickname, newEmail, newAvatarUrl);
        
        // 验证结果
        assertTrue(result);
        assertEquals(newNickname, existingUser.getNickname());
        assertEquals(newEmail, existingUser.getEmail());
        assertEquals(newAvatarUrl, existingUser.getAvatarUrl());
        verify(userService, times(1)).getById(userId);
        verify(userMapper, times(1)).checkEmailExists(newEmail);
        verify(userService, times(1)).updateById(existingUser);
    }
    
    @Test
    void testUpdateUserInfo_EmailExists() {
        // 准备测试数据
        Long userId = 1L;
        String newNickname = "Updated Nickname";
        String newEmail = "existing@example.com";
        String newAvatarUrl = "https://example.com/new-avatar.jpg";
        
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setNickname("Old Nickname");
        existingUser.setEmail("old@example.com");
        existingUser.setAvatarUrl("https://example.com/old-avatar.jpg");
        
        // 设置模拟行为
        when(userService.getById(userId)).thenReturn(existingUser);
        when(userMapper.checkEmailExists(newEmail)).thenReturn(1);
        
        // 执行测试
        boolean result = userService.updateUserInfo(userId, newNickname, newEmail, newAvatarUrl);
        
        // 验证结果
        assertFalse(result);
        // 确保用户信息没有被更新
        assertEquals("Old Nickname", existingUser.getNickname());
        assertEquals("old@example.com", existingUser.getEmail());
        assertEquals("https://example.com/old-avatar.jpg", existingUser.getAvatarUrl());
        verify(userService, times(1)).getById(userId);
        verify(userMapper, times(1)).checkEmailExists(newEmail);
        verify(userService, never()).updateById(any(User.class));
    }
    
    @Test
    void testUpdateUserInfo_UserNotFound() {
        // 准备测试数据
        Long userId = 999L;
        String newNickname = "Updated Nickname";
        String newEmail = "newemail@example.com";
        String newAvatarUrl = "https://example.com/new-avatar.jpg";
        
        // 设置模拟行为
        when(userService.getById(userId)).thenReturn(null);
        
        // 执行测试
        boolean result = userService.updateUserInfo(userId, newNickname, newEmail, newAvatarUrl);
        
        // 验证结果
        assertFalse(result);
        verify(userService, times(1)).getById(userId);
        verify(userMapper, never()).checkEmailExists(anyString());
        verify(userService, never()).updateById(any(User.class));
    }
}
