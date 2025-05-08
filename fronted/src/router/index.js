import { createRouter, createWebHistory } from 'vue-router'

// 导入页面组件
import Home from '../views/Home.vue'

// 定义路由
const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home
  },
  {
    path: '/login',
    name: 'Login',
    // 使用懒加载方式导入组件，提高性能
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/user',
    name: 'User',
    component: () => import('../views/User.vue'),
    meta: { requiresAuth: true } // 需要登录才能访问
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/Admin.vue'),
    meta: { requiresAuth: true, requiresAdmin: true } // 需要管理员权限
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('../views/ForgotPassword.vue')
  },
  {
    path: '/delete-account',
    name: 'DeleteAccount',
    component: () => import('../views/DeleteAccount.vue'),
    meta: { requiresAuth: true } // 需要登录才能访问
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫
router.beforeEach(async (to, from, next) => {
  // 检查本地存储中是否有token
  const token = localStorage.getItem('token')
  const isLoggedIn = token !== null
  const isAdmin = localStorage.getItem('isAdmin') === 'true'

  // 如果路由需要登录权限
  if (to.meta.requiresAuth) {
    if (!isLoggedIn) {
      // 未登录，重定向到登录页
      next({ name: 'Login', query: { redirect: to.fullPath } })
      return
    }

    // 如果需要管理员权限
    if (to.meta.requiresAdmin && !isAdmin) {
      // 不是管理员，重定向到首页
      next({ name: 'Home' })
      return
    }
  }

  // 如果已登录但访问登录、注册或找回密码页
  if (isLoggedIn && (to.name === 'Login' || to.name === 'Register' || to.name === 'ForgotPassword')) {
    // 重定向到首页
    next({ name: 'Home' })
    return
  }

  // 允许访问
  next()
})

export default router
