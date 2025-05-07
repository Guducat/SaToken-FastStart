package com.guducat.backend.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guducat.backend.entity.User;
import com.guducat.backend.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    // 存储重置密码令牌的映射，格式：<用户ID, 令牌>
    private final Map<Long, String> resetTokens = new ConcurrentHashMap<>();

    // 令牌过期时间（毫秒）
    private static final long TOKEN_EXPIRATION = 30 * 60 * 1000; // 30分钟

    // 定时清理过期令牌
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 存储令牌创建时间的映射，格式：<用户ID, 创建时间>
    private final Map<Long, Long> tokenCreationTimes = new ConcurrentHashMap<>();

    public UserService() {
        // 启动定时任务，每10分钟清理一次过期令牌
        scheduler.scheduleAtFixedRate(this::cleanExpiredTokens, 10, 10, TimeUnit.MINUTES);
    }

    /**
     * 用户登录
     *
     * @param account 账号（用户名或邮箱）
     * @param password 密码
     * @return 登录是否成功
     */
    public boolean login(String account, String password) {
        User user;

        // 先尝试使用用户名查询
        user = this.baseMapper.findByUsername(account);

        // 如果用户名查询不到，尝试使用邮箱查询
        if (user == null && account.contains("@")) {
            user = this.baseMapper.findByEmail(account);
        }

        if (user != null && user.getPassword().equals(SaSecureUtil.sha256(password))) {
            // 登录
            StpUtil.login(user.getId());
            return true;
        }
        return false;
    }

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param nickname 昵称
     * @param email 邮箱
     * @param avatarUrl 头像URL
     * @param password 密码
     * @return 注册是否成功
     */
    public boolean register(String username, String nickname, String email, String avatarUrl, String password) {
        // 检查用户名是否已存在
        User existingUser = this.baseMapper.findByUsername(username);
        if (existingUser != null) {
            return false;
        }

        // 检查邮箱是否已存在
        if (email != null && !email.isEmpty()) {
            int emailCount = this.baseMapper.checkEmailExists(email);
            if (emailCount > 0) {
                return false;
            }
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setNickname(nickname);
        user.setEmail(email);
        user.setAvatarUrl(avatarUrl);
        // 使用相同的密码加密方式
        user.setPassword(SaSecureUtil.sha256(password));
        // 设置默认角色
        user.setRole("user");

        // 保存用户并返回结果
        return save(user);
    }

    /**
     * 根据ID获取用户信息
     *
     * @param id 用户ID
     * @return 用户对象，如果不存在则返回null
     */
    public User getUserById(Long id) {
        return getById(id);
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户对象，如果不存在则返回null
     */
    public User getUserByUsername(String username) {
        return this.baseMapper.findByUsername(username);
    }

    /**
     * 更新用户信息
     *
     * @param id 用户ID
     * @param nickname 昵称
     * @param email 邮箱
     * @param avatarUrl 头像URL
     * @return 更新是否成功
     */
    public boolean updateUserInfo(Long id, String nickname, String email, String avatarUrl) {
        // 获取当前用户
        User user = getById(id);
        if (user == null) {
            return false;
        }

        // 检查邮箱是否已被其他用户使用
        if (email != null && !email.isEmpty() && !email.equals(user.getEmail())) {
            int emailCount = this.baseMapper.checkEmailExists(email);
            if (emailCount > 0) {
                return false;
            }
        }

        // 更新用户信息
        if (nickname != null && !nickname.isEmpty()) {
            user.setNickname(nickname);
        }

        if (email != null && !email.isEmpty()) {
            user.setEmail(email);
        }

        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }

        // 保存更新
        return updateById(user);
    }

    /**
     * 重置用户密码
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 重置是否成功
     */
    public boolean resetPassword(Long userId, String newPassword) {
        // 获取用户
        User user = getById(userId);
        if (user == null) {
            return false;
        }

        // 更新密码
        user.setPassword(SaSecureUtil.sha256(newPassword));

        // 保存更新
        return updateById(user);
    }

    /**
     * 清理过期的重置令牌
     */
    private void cleanExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        tokenCreationTimes.forEach((userId, creationTime) -> {
            if (currentTime - creationTime > TOKEN_EXPIRATION) {
                resetTokens.remove(userId);
                tokenCreationTimes.remove(userId);
            }
        });
    }

    /**
     * 生成重置密码令牌
     *
     * @param userId 用户ID
     * @return 重置令牌
     */
    public String generateResetToken(Long userId) {
        String token = UUID.randomUUID().toString();
        resetTokens.put(userId, token);
        tokenCreationTimes.put(userId, System.currentTimeMillis());
        return token;
    }

    /**
     * 验证重置令牌
     *
     * @param userId 用户ID
     * @param token 重置令牌
     * @return 验证是否成功
     */
    public boolean verifyResetToken(Long userId, String token) {
        // 检查令牌是否存在
        if (!resetTokens.containsKey(userId)) {
            return false;
        }

        // 检查令牌是否匹配
        String storedToken = resetTokens.get(userId);
        if (!storedToken.equals(token)) {
            return false;
        }

        // 检查令牌是否过期
        long creationTime = tokenCreationTimes.getOrDefault(userId, 0L);
        return System.currentTimeMillis() - creationTime <= TOKEN_EXPIRATION;
    }

    /**
     * 移除重置令牌
     *
     * @param userId 用户ID
     */
    public void removeResetToken(Long userId) {
        resetTokens.remove(userId);
        tokenCreationTimes.remove(userId);
    }
}
