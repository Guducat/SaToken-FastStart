package com.guducat.backend.config;

import cn.dev33.satoken.stp.StpInterface;
import com.guducat.backend.entity.User;
import com.guducat.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限验证接口扩展
 */
@Component
public class StpInterfaceImpl implements StpInterface {
    
    @Autowired
    private UserService userService;
    
    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 暂时返回一个空的权限列表
        return new ArrayList<>();
    }
    
    /**
     * 返回一个账号所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roleList = new ArrayList<>();
        
        // 获取用户信息
        User user = userService.getById(Long.parseLong(loginId.toString()));
        if (user != null) {
            // 根据用户角色添加对应的角色标识
            if ("admin".equals(user.getRole())) {
                roleList.add("admin");
            } else {
                roleList.add("user");
            }
        }
        
        return roleList;
    }
}
