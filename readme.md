# SaToken-Vue3快速启动

[![Vue.js](https://img.shields.io/badge/Vue.js-4.1-4FC08D?style=flat-square&logo=vue.js&logoColor=white)](https://vuejs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-6DB33F?style=flat-square&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Sa-Token](https://img.shields.io/badge/Sa--Token-1.42.0-blue?style=flat-square)](https://sa-token.dev33.cn/)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.3-red?style=flat-square)](https://baomidou.com/)
[![TailwindCSS](https://img.shields.io/badge/TailwindCSS-4.1.4-38B2AC?style=flat-square&logo=tailwind-css&logoColor=white)](https://tailwindcss.com/)
[![MySQL](https://img.shields.io/badge/MySQL-8.3-4479A1?style=flat-square&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![GitHub](https://img.shields.io/badge/GitHub-SaToken--FastStart-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/Guducat/SaToken-FastStart)

## 项目简介

本项目是以 Spring Boot 3 + Vue 3 为技术栈，使用 MySQL/PostgreSQL 数据库集成 MyBatis-Plus 与 Sa-Token，旨在提供一个轻量化的前后端分离登录认证框架。
前端使用 TailwindCSS 和 Headless UI 构建现代化的用户界面。

前端仅供功能测试，功能缺失且简陋，实际开发请勿使用！

### 功能特点

- ✅ 用户认证与授权（Sa-Token）
- ✅ 用户注册与登录（支持用户名或邮箱登录）
- ✅ 用户信息管理（个人信息编辑）
- ✅ 角色权限控制（管理员与普通用户）
- ✅ 响应式设计（支持移动端和桌面端）
- ✅ 组件化架构（头部、页脚等独立组件）

## 快速开始

### 环境要求

- JDK 17+
- Node.js 16+
- MySQL 8.0+ 或 PostgreSQL 12+ 等主流数据库（需要自行修改配置）

### 数据库配置

1. 创建数据库和表

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS satoken_vue3 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE satoken_vue3;

-- 创建用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `role` varchar(20) DEFAULT 'user' COMMENT '权限',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建管理员账号
-- 密码为 admin，使用 SHA-256 加密
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `email`) VALUES
('admin', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', '管理员', 'admin', 'admin@example.com');
```

2. 修改后端数据库配置

编辑 `backend/src/main/resources/application.properties` 文件，配置数据库连接信息：

```properties
# 数据库配置
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/数据库名?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=your_password
```

### 运行项目

1. 启动后端

```bash
cd backend
mvn spring-boot:run
```

2. 启动前端

```bash
cd fronted
npm install
npm run dev
```

3. 访问前端应用

打开浏览器访问：`http://localhost:5173`

### 默认账号

- 管理员账号：`admin`
- 密码：`admin`

## 项目结构

```
SaToken-Vue3/
├── backend/              # 后端项目
│   ├── src/main/java/    # Java 源代码
│   │   ├── config/       # 配置类
│   │   ├── controller/   # 控制器
│   │   ├── entity/       # 实体类
│   │   ├── mapper/       # MyBatis 映射器
│   │   └── service/      # 服务层
│   └── src/main/resources/ # 资源文件
│       └── application.properties # 应用配置
├── fronted/               # 前端项目
│   ├── public/           # 静态资源
│   ├── src/              # 源代码
│   │   ├── api/          # API 调用
│   │   ├── components/    # 组件
│   │   ├── router/        # 路由
│   │   └── views/         # 页面
│   ├── index.html        # HTML 模板
│   ├── package.json      # 依赖配置
│   └── vite.config.js     # Vite 配置
└── readme.md              # 项目说明
```

## 贡献指南

欢迎提交 [Issue](https://github.com/Guducat/SaToken-FastStart/issues) 或 [Pull Request](https://github.com/Guducat/SaToken-FastStart/pulls),让我们一起改进此快速启动项目。

## 许可证

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg?style=flat)](https://opensource.org/licenses/MIT)