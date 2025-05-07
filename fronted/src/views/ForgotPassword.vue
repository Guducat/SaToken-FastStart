<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '../components/AppHeader.vue'
import api from '../api'

const router = useRouter()

// 步骤状态
const step = ref(1) // 1: 验证身份, 2: 重置密码

// 表单数据
const verifyForm = reactive({
  username: '',
  email: ''
})

const resetForm = reactive({
  userId: null,
  newPassword: '',
  confirmPassword: '',
  token: '' // 添加令牌字段
})

// 错误和成功消息
const errorMsg = ref('')
const successMsg = ref('')

// 验证身份
const verifyIdentity = async () => {
  errorMsg.value = ''

  if (!verifyForm.username || !verifyForm.email) {
    errorMsg.value = '用户名和邮箱不能为空'
    return
  }

  try {
    const response = await api.verifyIdentity(verifyForm.username, verifyForm.email)

    if (response.code === 200) {
      // 验证成功，进入重置密码步骤
      resetForm.userId = response.data.userId
      resetForm.token = response.data.resetToken // 保存令牌
      step.value = 2
      successMsg.value = '身份验证成功，请设置新密码'
      setTimeout(() => {
        successMsg.value = ''
      }, 3000)
    } else {
      errorMsg.value = response.msg || '验证失败，请检查用户名和邮箱'
    }
  } catch (error) {
    console.error('Identity verification error:', error)
    errorMsg.value = '验证失败，请稍后再试'
  }
}

// 重置密码
const resetPassword = async () => {
  errorMsg.value = ''

  if (!resetForm.newPassword || !resetForm.confirmPassword) {
    errorMsg.value = '新密码和确认密码不能为空'
    return
  }

  if (resetForm.newPassword !== resetForm.confirmPassword) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }

  try {
    const response = await api.resetPassword(
      resetForm.userId,
      resetForm.newPassword,
      resetForm.confirmPassword,
      resetForm.token // 传递令牌
    )

    if (response.code === 200) {
      // 重置成功，跳转到登录页
      successMsg.value = '密码重置成功，即将跳转到登录页...'
      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } else {
      errorMsg.value = response.msg || '密码重置失败'
    }
  } catch (error) {
    console.error('Password reset error:', error)
    errorMsg.value = '密码重置失败，请稍后再试'
  }
}

// 返回上一步
const goBack = () => {
  if (step.value > 1) {
    step.value--
    errorMsg.value = ''
    successMsg.value = ''
  } else {
    router.push('/login')
  }
}
</script>

<template>
  <div class="bg-white">
    <AppHeader />

    <div class="flex min-h-screen flex-1 flex-col justify-center px-6 py-12 lg:px-8">
      <div class="sm:mx-auto sm:w-full sm:max-w-sm">
        <h2 class="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">找回密码</h2>
      </div>

      <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
        <!-- 步骤指示器 -->
        <div class="mb-8">
          <div class="flex items-center justify-center">
            <div class="flex items-center text-blue-600 relative">
              <div class="rounded-full transition duration-500 ease-in-out h-12 w-12 py-3 border-2 border-blue-600 flex items-center justify-center bg-blue-600 text-white">
                1
              </div>
              <div class="absolute top-0 -ml-10 text-center mt-16 w-32 text-xs font-medium text-blue-600">验证身份</div>
            </div>
            <div class="flex-auto border-t-2" :class="step >= 2 ? 'border-blue-600' : 'border-gray-300'"></div>
            <div class="flex items-center relative">
              <div class="rounded-full transition duration-500 ease-in-out h-12 w-12 py-3 border-2 flex items-center justify-center"
                   :class="step >= 2 ? 'border-blue-600 bg-blue-600 text-white' : 'border-gray-300 text-gray-500'">
                2
              </div>
              <div class="absolute top-0 -ml-10 text-center mt-16 w-32 text-xs font-medium"
                   :class="step >= 2 ? 'text-blue-600' : 'text-gray-500'">重置密码</div>
            </div>
          </div>
        </div>

        <!-- 成功消息 -->
        <div v-if="successMsg" class="mb-4 p-3 bg-green-100 text-green-700 rounded-md text-sm">
          {{ successMsg }}
        </div>

        <!-- 错误消息 -->
        <div v-if="errorMsg" class="mb-4 p-3 bg-red-100 text-red-700 rounded-md text-sm">
          {{ errorMsg }}
        </div>

        <!-- 步骤1: 验证身份 -->
        <form v-if="step === 1" @submit.prevent="verifyIdentity" class="space-y-6">
          <div>
            <label for="username" class="block text-sm font-medium leading-6 text-gray-900">用户名</label>
            <div class="mt-2">
              <input
                id="username"
                name="username"
                type="text"
                v-model="verifyForm.username"
                required
                placeholder="请输入用户名"
                class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 px-3"
              />
            </div>
          </div>

          <div>
            <label for="email" class="block text-sm font-medium leading-6 text-gray-900">邮箱</label>
            <div class="mt-2">
              <input
                id="email"
                name="email"
                type="email"
                v-model="verifyForm.email"
                required
                placeholder="请输入邮箱"
                class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 px-3"
              />
            </div>
          </div>

          <div class="flex justify-between space-x-4">
            <button
              type="button"
              @click="goBack"
              class="flex w-1/2 justify-center rounded-md bg-gray-500 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-gray-600 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-gray-600"
            >
              返回
            </button>
            <button
              type="submit"
              class="flex w-1/2 justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
            >
              下一步
            </button>
          </div>
        </form>

        <!-- 步骤2: 重置密码 -->
        <form v-if="step === 2" @submit.prevent="resetPassword" class="space-y-6">
          <div>
            <label for="newPassword" class="block text-sm font-medium leading-6 text-gray-900">新密码</label>
            <div class="mt-2">
              <input
                id="newPassword"
                name="newPassword"
                type="password"
                v-model="resetForm.newPassword"
                required
                placeholder="请输入新密码"
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
                v-model="resetForm.confirmPassword"
                required
                placeholder="请再次输入新密码"
                class="block w-full rounded-md border-0 py-1.5 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6 px-3"
              />
            </div>
          </div>

          <div class="flex justify-between space-x-4">
            <button
              type="button"
              @click="goBack"
              class="flex w-1/2 justify-center rounded-md bg-gray-500 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-gray-600 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-gray-600"
            >
              上一步
            </button>
            <button
              type="submit"
              class="flex w-1/2 justify-center rounded-md bg-indigo-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-indigo-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600"
            >
              重置密码
            </button>
          </div>
        </form>

        <p class="mt-10 text-center text-sm text-gray-500">
          <router-link to="/login" class="font-semibold leading-6 text-indigo-600 hover:text-indigo-500">返回登录</router-link>
        </p>
      </div>
    </div>
  </div>
</template>
