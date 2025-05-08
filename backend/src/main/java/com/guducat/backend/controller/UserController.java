package com.guducat.backend.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

import java.util.HashMap;
import com.guducat.backend.entity.User;
import com.guducat.backend.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户控制器，用于处理与用户相关的请求。
 * <p>
 * 包含用户登录、注册、信息管理等功能的接口。
 * </p>
 *
 * @author 孤独豹猫
 * @version 1.0
 * @since 2025-05
 */

@RestController
@RequestMapping("/user/")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * 用户登录接口。
     * <p>
     * 模拟用户登录逻辑，待开发结合数据库验证用户名密码并进行安全处理。
     * </p>
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果信息并推送token
     */
    @RequestMapping("doLogin")
    public SaResult doLogin(String username, String password) {
        boolean loginSuccess = userService.login(username, password);
        if (loginSuccess){
            SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
            return SaResult.data(tokenInfo);
        }
        return SaResult.error("登录失败，请检查用户名或密码");
    }

    /**
     * 用户注册接口。
     * <p>
     * 提供新用户注册功能，包含用户名、昵称、邮箱、头像URL、密码等信息的注册。
     * </p>
     *
     * @param username 用户名（英文，可作为账号登录）
     * @param nickname 昵称（显示名称）
     * @param email 邮箱（可作为账号登录）
     * @param avatarUrl 头像URL（选填）
     * @param password 密码
     * @param confirmPassword 确认密码
     * @return 注册结果
     */
    @RequestMapping("doRegister")
    public SaResult doRegister(String username, String nickname, String email,
                              String avatarUrl, String password, String confirmPassword) {
        // 验证密码是否一致
        if (!password.equals(confirmPassword)) {
            return SaResult.error("两次输入的密码不一致");
        }

        boolean registerSuccess = userService.register(username, nickname, email, avatarUrl, password);
        if (registerSuccess) {
            boolean loginSuccess = userService.login(username, password);
            if (loginSuccess) {
                SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
                return SaResult.data(tokenInfo);
            }
            return SaResult.error("注册成功，但自动登录失败，请手动登录");
        }
        return SaResult.error("注册失败，用户名或邮箱可能已存在");
    }


    /**
     * 获取当前登录用户信息。
     * <p>
     * 返回当前登录用户的详细信息，包括ID、用户名、昵称、邮箱、头像URL等。
     * </p>
     *
     * @return 用户信息
     */
    @RequestMapping("getInfo")
    public SaResult getInfo() {
        // 获取当前登录用户ID，如果未登录则返回错误
        Object loginId = StpUtil.getLoginId(-1);
        if (loginId.equals(-1)) {
            return SaResult.error("用户未登录");
        }

        // 获取用户信息
        User user = userService.getUserById(Long.parseLong(loginId.toString()));
        if (user == null) {
            return SaResult.error("用户信息不存在");
        }

        // 清除敏感信息
        user.setPassword(null);

        return SaResult.data(user);
    }

    /**
     * 更新当前登录用户信息。
     * <p>
     * 支持更新昵称、邮箱、头像URL等信息。
     * </p>
     *
     * @param nickname 昵称
     * @param email 邮箱
     * @param avatarUrl 头像URL
     * @return 更新结果
     */
    @RequestMapping("updateInfo")
    public SaResult updateInfo(String nickname, String email, String avatarUrl) {
        // 获取当前登录用户ID，如果未登录则返回错误
        Object loginId = StpUtil.getLoginId(-1);
        if (loginId.equals(-1)) {
            return SaResult.error("用户未登录");
        }

        // 更新用户信息
        boolean updateSuccess = userService.updateUserInfo(
            Long.parseLong(loginId.toString()),
            nickname,
            email,
            avatarUrl
        );

        if (updateSuccess) {
            return SaResult.ok("用户信息更新成功");
        } else {
            return SaResult.error("用户信息更新失败，邮箱可能已被使用");
        }
    }

    /**
     * 查询当前会话的登录状态。
     *
     * @return true/false
     */
    @RequestMapping("isLogin")
    public String isLogin() {
        return "当前会话是否登录：" + StpUtil.isLogin();
    }

    /**
     * 查询当前用户是否拥有权限。
     *
     * @return true/false
     */
    @RequestMapping("isAdmin")
    public String isAdmin(){
        return "当前角色是否管理员：" + StpUtil.hasRole("admin");
    }

    /**
     * 验证用户名和邮箱是否匹配。
     * <p>
     * 用于找回密码功能的第一步，验证用户身份。
     * </p>
     *
     * @param username 用户名
     * @param email 邮箱
     * @return 验证结果
     */
    @RequestMapping("verifyIdentity")
    public SaResult verifyIdentity(String username, String email) {
        // 参数验证
        if (username == null || username.isEmpty() || email == null || email.isEmpty()) {
            return SaResult.error("用户名和邮箱不能为空");
        }

        // 查询用户
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return SaResult.error("用户不存在");
        }

        // 验证邮箱是否匹配
        if (!email.equals(user.getEmail())) {
            return SaResult.error("邮箱与用户不匹配");
        }

        // 验证成功，生成一个临时令牌，用于重置密码
        String resetToken = userService.generateResetToken(user.getId());

        // 返回用户ID和重置令牌
        return SaResult.data(new HashMap<String, Object>() {{
            put("userId", user.getId());
            put("resetToken", resetToken);
        }});
    }

    /**
     * 重置密码。
     * <p>
     * 用于找回密码功能的第二步，重置用户密码。
     * </p>
     *
     * @param userId 用户ID
     * @param newPassword 新密码
     * @param confirmPassword 确认密码
     * @return 重置结果
     */
    @RequestMapping("resetPassword")
    public SaResult resetPassword(Long userId, String newPassword, String confirmPassword, String token) {
        // 参数验证
        if (userId == null || newPassword == null || newPassword.isEmpty() ||
            confirmPassword == null || confirmPassword.isEmpty() || token == null || token.isEmpty()) {
            return SaResult.error("参数不完整");
        }

        // 验证两次密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            return SaResult.error("两次输入的密码不一致");
        }

        // 验证重置令牌
        if (!userService.verifyResetToken(userId, token)) {
            return SaResult.error("重置令牌无效或已过期");
        }

        // 重置密码
        boolean success = userService.resetPassword(userId, newPassword);
        if (success) {
            // 重置成功后删除令牌
            userService.removeResetToken(userId);
            return SaResult.ok("密码重置成功");
        } else {
            return SaResult.error("密码重置失败");
        }
    }

    /**
     * 注销当前登录用户账户。
     * <p>
     * 此操作将永久删除用户账户，操作不可逆，请谨慎使用。
     * 前端需要进行三次确认才能调用此接口。
     * </p>
     *
     * @return 注销结果
     */
    @RequestMapping("deleteAccount")
    public SaResult deleteAccount() {
        // 获取当前登录用户ID，如果未登录则返回错误
        Object loginId = StpUtil.getLoginId(-1);
        if (loginId.equals(-1)) {
            return SaResult.error("用户未登录");
        }

        Long userId = Long.parseLong(loginId.toString());

        // 执行账户注销
        boolean success = userService.deleteAccount(userId);

        if (success) {
            return SaResult.ok("账户已成功注销");
        } else {
            return SaResult.error("账户注销失败");
        }
    }
}

