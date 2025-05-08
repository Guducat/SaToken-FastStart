import axios from 'axios';

// 创建 axios 实例
const api = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器 - 添加 token
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['satoken'] = token;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器 - 处理错误
api.interceptors.response.use(
  response => {
    return response.data;
  },
  error => {
    console.error('API Error:', error);
    return Promise.reject(error);
  }
);

// API 方法
export default {
  // 用户登录
  login(account, password) {
    return api.get('/user/doLogin', { params: { username: account, password } });
  },

  // 用户注册
  register(userData) {
    return api.get('/user/doRegister', { params: userData });
  },

  // 获取用户信息
  getUserInfo() {
    return api.get('/user/getInfo');
  },

  // 更新用户信息
  updateUserInfo(userData) {
    return api.get('/user/updateInfo', { params: userData });
  },

  // 检查登录状态
  isLogin() {
    return api.get('/user/isLogin');
  },

  // 检查是否是管理员
  isAdmin() {
    return api.get('/user/isAdmin', { transformResponse: [data => data] }); // 保持原始文本响应
  },

  // 验证用户身份（用于找回密码）
  verifyIdentity(username, email) {
    return api.get('/user/verifyIdentity', { params: { username, email } });
  },

  // 重置密码
  resetPassword(userId, newPassword, confirmPassword, token) {
    return api.get('/user/resetPassword', {
      params: {
        userId,
        newPassword,
        confirmPassword,
        token // 添加验证令牌
      }
    });
  },

  // 注销账户
  deleteAccount() {
    return api.get('/user/deleteAccount');
  }
};
