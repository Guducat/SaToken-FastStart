<script setup>
import AppHeader from '../components/AppHeader.vue'
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'

const router = useRouter()
const username = ref('')
const nickname = ref('')
const email = ref('')
const avatarUrl = ref('')
const password = ref('')
const confirmPassword = ref('')
const errorMsg = ref('')

const register = async () => {
  // 表单验证
  if (password.value !== confirmPassword.value) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }
  
  if (!username.value) {
    errorMsg.value = '用户名不能为空'
    return
  }
  
  try {
    const userData = {
      username: username.value,
      nickname: nickname.value,
      email: email.value,
      avatarUrl: avatarUrl.value,
      password: password.value,
      confirmPassword: confirmPassword.value
    }
    
    const data = await api.register(userData);
    
    if (data.code === 200) {
      // 注册成功，保存token信息
      localStorage.setItem('token', data.data.tokenValue);
      localStorage.setItem('username', username.value);
      
      // 跳转到首页
      router.push('/');
    } else {
      errorMsg.value = data.msg || '注册失败，请稍后再试';
    }
  } catch (error) {
    console.error('Registration error:', error);
    errorMsg.value = '注册失败，请稍后再试';
  }
}
</script>

<template>
  <div class="bg-white">
    <AppHeader />
    
    <div class="flex min-h-screen flex-1 flex-col justify-center px-6 py-12 lg:px-8">
      <div class="sm:mx-auto sm:w-full sm:max-w-sm">
        <h2 class="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">用户注册</h2>
      </div>

      <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
        <form @submit.prevent="register" class="space-y-6">
          <div>
            <label for="username" class="block text-sm font-medium leading-6 text-gray-900">用户名（英文，可作为账号登录）</label>
            <div class="mt-2">
              <input 
                id="username" 
                name="username" 
                type="text" 
                v-model="username"
                required 
                placeholder="请输入用户名"
                class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 px-3"
              />
            </div>
          </div>

          <div>
            <label for="nickname" class="block text-sm font-medium leading-6 text-gray-900">昵称（显示名称）</label>
            <div class="mt-2">
              <input 
                id="nickname" 
                name="nickname" 
                type="text" 
                v-model="nickname"
                placeholder="请输入昵称"
                class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 px-3"
              />
            </div>
          </div>

          <div>
            <label for="email" class="block text-sm font-medium leading-6 text-gray-900">邮箱（可作为账号登录）</label>
            <div class="mt-2">
              <input 
                id="email" 
                name="email" 
                type="email" 
                v-model="email"
                placeholder="请输入邮箱"
                class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 px-3"
              />
            </div>
          </div>

          <div>
            <label for="avatarUrl" class="block text-sm font-medium leading-6 text-gray-900">头像URL（选填）</label>
            <div class="mt-2">
              <input 
                id="avatarUrl" 
                name="avatarUrl" 
                type="url" 
                v-model="avatarUrl"
                placeholder="请输入头像URL"
                class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 px-3"
              />
            </div>
          </div>

          <div>
            <label for="password" class="block text-sm font-medium leading-6 text-gray-900">密码</label>
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

          <div>
            <label for="confirmPassword" class="block text-sm font-medium leading-6 text-gray-900">确认密码</label>
            <div class="mt-2">
              <input 
                id="confirmPassword" 
                name="confirmPassword" 
                type="password" 
                v-model="confirmPassword"
                required 
                placeholder="请再次输入密码"
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
              注册
            </button>
          </div>
        </form>

        <p class="mt-10 text-center text-sm text-gray-500">
          已有账号？
          <router-link to="/login" class="font-semibold leading-6 text-indigo-600 hover:text-indigo-500">立即登录</router-link>
        </p>
        <p class="mt-2 text-center text-sm text-gray-500">
          <router-link to="/" class="font-semibold leading-6 text-gray-600 hover:text-gray-500">返回首页</router-link>
        </p>
      </div>
    </div>
  </div>
</template>
