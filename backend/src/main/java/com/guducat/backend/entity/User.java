package com.guducat.backend.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    /**
     * -- SETTER --
     *  设置用户头像 URL
     *
     * @param avatarUrl 头像 URL
     */
    private String avatarUrl;
    private String email;
    private String role;

    /**
     * 获取用户头像 URL
     * 如果用户没有设置头像，返回默认头像 URL
     *
     * @return 头像 URL
     */
    public String getAvatarUrl() {
        // 如果头像 URL 为空，返回默认头像
        if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
            return "https://tse3-mm.cn.bing.net/th/id/OIP-C.1nbiDZSh4TGfU2F8Qwe4QgHaHa?cb=iwc1&rs=1&pid=ImgDetMain";
        }
        return avatarUrl;
    }

}
