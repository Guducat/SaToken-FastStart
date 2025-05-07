<script setup>
import AppHeader from '../components/AppHeader.vue'
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'

const router = useRouter()
const account = ref('') // 可以是用户名或邮箱
const password = ref('')
const errorMsg = ref('')

const login = async () => {
  if (!account.value || !password.value) {
    errorMsg.value = '账号和密码不能为空'
    return
  }

  try {
    console.log('开始登录，账号:', account.value)
    const data = await api.login(account.value, password.value)
    console.log('登录响应数据:', data)

    if (data.code === 200) {
      // 登录成功，保存token信息
      console.log('保存token信息:', data.data)
      localStorage.setItem('token', data.data.tokenValue)
      localStorage.setItem('username', account.value)

      // 检查是否是管理员
      try {
        const adminCheck = await api.isAdmin()
        console.log('管理员检查结果:', adminCheck)
        // 检查是否包含“当前角色是否管理员：true”字符串
        if (adminCheck.includes('当前角色是否管理员：true')) {
          localStorage.setItem('isAdmin', 'true')
          console.log('设置管理员标记成功')
        } else {
          localStorage.removeItem('isAdmin')
          console.log('非管理员账号')
        }
      } catch (e) {
        console.error('Failed to check admin status:', e)
        localStorage.removeItem('isAdmin')
      }

      // 如果有重定向参数，则跳转到重定向页面，否则跳转到首页
      const redirectPath = router.currentRoute.value.query.redirect || '/'
      router.push(redirectPath)
    } else {
      errorMsg.value = data.msg || '登录失败，请检查账号和密码'
    }
  } catch (error) {
    console.error('Login error:', error)
    errorMsg.value = '登录失败，请检查账号和密码'
  }
}
</script>

<template>
  <div class="bg-white">
    <AppHeader />

    <div class="flex min-h-screen flex-1 flex-col justify-center px-6 py-12 lg:px-8">
      <div class="sm:mx-auto sm:w-full sm:max-w-sm">
        <h2 class="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">用户登录</h2>
      </div>

      <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
        <form @submit.prevent="login" class="space-y-6">
          <div>
            <label for="account" class="block text-sm font-medium leading-6 text-gray-900">账号</label>
            <div class="mt-2">
              <input
                id="account"
                name="account"
                type="text"
                v-model="account"
                required
                placeholder="请输入用户名或邮箱"
                class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 px-3"
              />
            </div>
          </div>

          <div>
            <div class="flex items-center justify-between">
              <label for="password" class="block text-sm font-medium leading-6 text-gray-900">密码</label>
              <div class="text-sm">
                <router-link to="/forgot-password" class="font-semibold text-indigo-600 hover:text-indigo-500">忘记密码?</router-link>
              </div>
            </div>
            <div class="mt-2">
              <input
                id="password"
                name="password"
                type="password"
                v-model="password"
                required
                placeholder="请输入密码"
                class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 px-3"
              />
            </div>
          </div>

          <div v-if="errorMsg" class="p-3 bg-red-100 text-red-700 rounded-md text-sm">
            {{ errorMsg }}
          </div>

          <div>
            <button
              type="submit"
              class="flex w-full justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
            >
              登录
            </button>
          </div>
        </form>

        <p class="mt-10 text-center text-sm text-gray-500">
          没有账号？
          <router-link to="/register" class="font-semibold leading-6 text-indigo-600 hover:text-indigo-500">立即注册</router-link>
        </p>
        <p class="mt-2 text-center text-sm text-gray-500">
          <router-link to="/" class="font-semibold leading-6 text-gray-600 hover:text-gray-500">返回首页</router-link>
        </p>
      </div>
    </div>
  </div>
</template>
