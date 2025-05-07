// 基于原生 fetch 的 API 服务（保留作为参考）
const API_BASE_URL = 'http://localhost:8081';

// 创建请求头
const createHeaders = () => {
  const headers = {
    'Content-Type': 'application/x-www-form-urlencoded',
  };
  
  // 如果有token，添加到请求头
  const token = localStorage.getItem('token');
  if (token) {
    headers['satoken'] = token;
  }
  
  return headers;
};

// 将对象转换为 URL 参数格式
const objectToUrlParams = (obj) => {
  return Object.keys(obj)
    .filter(key => obj[key] !== undefined && obj[key] !== null)
    .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(obj[key])}`)
    .join('&');
};

// API 方法
export default {
  // 用户登录
  async login(username, password) {
    const params = objectToUrlParams({ username, password });
    const response = await fetch(`${API_BASE_URL}/user/doLogin?${params}`, {
      method: 'GET',
      headers: createHeaders()
    });
    return await response.json();
  },
  
  // 用户注册
  async register(userData) {
    const params = objectToUrlParams(userData);
    const response = await fetch(`${API_BASE_URL}/user/doRegister?${params}`, {
      method: 'GET',
      headers: createHeaders()
    });
    return await response.json();
  },
  
  // 获取用户信息
  async getUserInfo() {
    const response = await fetch(`${API_BASE_URL}/user/getInfo`, {
      method: 'GET',
      headers: createHeaders()
    });
    return await response.json();
  },
  
  // 更新用户信息
  async updateUserInfo(userData) {
    const params = objectToUrlParams(userData);
    const response = await fetch(`${API_BASE_URL}/user/updateInfo?${params}`, {
      method: 'GET',
      headers: createHeaders()
    });
    return await response.json();
  },
  
  // 检查登录状态
  async isLogin() {
    const response = await fetch(`${API_BASE_URL}/user/isLogin`, {
      method: 'GET',
      headers: createHeaders()
    });
    return await response.json();
  },
  
  // 检查是否是管理员
  async isAdmin() {
    const response = await fetch(`${API_BASE_URL}/user/isAdmin`, {
      method: 'GET',
      headers: createHeaders()
    });
    // isAdmin 接口返回的是纯文本，不是JSON
    return await response.text();
  },
  
  // 验证用户身份（用于找回密码）
  async verifyIdentity(username, email) {
    const params = objectToUrlParams({ username, email });
    const response = await fetch(`${API_BASE_URL}/user/verifyIdentity?${params}`, {
      method: 'GET',
      headers: createHeaders()
    });
    return await response.json();
  },
  
  // 重置密码
  async resetPassword(userId, newPassword, confirmPassword, token) {
    const params = objectToUrlParams({ userId, newPassword, confirmPassword, token });
    const response = await fetch(`${API_BASE_URL}/user/resetPassword?${params}`, {
      method: 'GET',
      headers: createHeaders()
    });
    return await response.json();
  }
};
