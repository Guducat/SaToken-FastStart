package com.guducat.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guducat.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    /**
     * 根据用户名和密码查询用户
     */
    @Select("SELECT * FROM user WHERE username = #{username} AND password = #{password}")
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    /**
     * 检查用户名是否已存在
     */
    @Select("SELECT COUNT(*) FROM user WHERE username = #{username}")
    int checkUsernameExists(@Param("username") String username);

    /**
     * 检查邮箱是否已存在
     */
    @Select("SELECT COUNT(*) FROM user WHERE email = #{email}")
    int checkEmailExists(@Param("email") String email);
}