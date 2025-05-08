<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '../components/AppHeader.vue'
import api from '../api'

const router = useRouter()

// 确认步骤状态
const step = ref(1) // 1: 第一次确认, 2: 第二次确认, 3: 第三次确认
const isProcessing = ref(false) // 是否正在处理请求
const countdown = ref(5) // 冷静期倒计时（秒）
const countdownActive = ref(false) // 倒计时是否激活

// 错误和成功消息
const errorMsg = ref('')
const successMsg = ref('')

// 启动倒计时
const startCountdown = () => {
  countdown.value = 5
  countdownActive.value = true
  
  const timer = setInterval(() => {
    countdown.value--
    
    if (countdown.value <= 0) {
      clearInterval(timer)
      countdownActive.value = false
    }
  }, 1000)
}

// 第一次确认
const firstConfirm = () => {
  errorMsg.value = ''
  step.value = 2
  startCountdown()
}

// 第二次确认
const secondConfirm = () => {
  errorMsg.value = ''
  step.value = 3
  startCountdown()
}

// 第三次确认并执行注销
const finalConfirm = async () => {
  if (isProcessing.value) return
  
  errorMsg.value = ''
  isProcessing.value = true
  
  try {
    const response = await api.deleteAccount()
    
    if (response.code === 200) {
      // 注销成功
      successMsg.value = '账户已成功注销，即将跳转到登录页...'
      
      // 清除本地存储的用户信息
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('isAdmin')
      
      // 延迟跳转到登录页
      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } else {
      errorMsg.value = response.msg || '账户注销失败，请稍后再试'
      isProcessing.value = false
    }
  } catch (error) {
    console.error('Account deletion error:', error)
    errorMsg.value = '账户注销失败，请稍后再试'
    isProcessing.value = false
  }
}

// 取消注销
const cancelDeletion = () => {
  router.push('/user')
}

// 检查登录状态
onMounted(() => {
  if (!localStorage.getItem('token')) {
    router.push('/login')
  }
})
</script>

<template>
  <div class="bg-white">
    <AppHeader />

    <div class="flex min-h-screen flex-1 flex-col justify-center px-6 py-12 lg:px-8">
      <div class="sm:mx-auto sm:w-full sm:max-w-sm">
        <h2 class="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">注销账户</h2>
        <p class="mt-2 text-center text-sm text-red-600">此操作将永久删除您的账户，所有数据将无法恢复</p>
      </div>

      <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-sm">
        <!-- 错误消息 -->
        <div v-if="errorMsg" class="mb-4 rounded-md bg-red-50 p-4">
          <div class="flex">
            <div class="ml-3">
              <h3 class="text-sm font-medium text-red-800">错误</h3>
              <div class="mt-2 text-sm text-red-700">
                <p>{{ errorMsg }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 成功消息 -->
        <div v-if="successMsg" class="mb-4 rounded-md bg-green-50 p-4">
          <div class="flex">
            <div class="ml-3">
              <h3 class="text-sm font-medium text-green-800">成功</h3>
              <div class="mt-2 text-sm text-green-700">
                <p>{{ successMsg }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 第一步确认 -->
        <div v-if="step === 1" class="space-y-6">
          <div class="rounded-md bg-yellow-50 p-4">
            <div class="flex">
              <div class="ml-3">
                <h3 class="text-sm font-medium text-yellow-800">警告</h3>
                <div class="mt-2 text-sm text-yellow-700">
                  <p>您即将注销您的账户。此操作将：</p>
                  <ul class="mt-2 list-disc pl-5 space-y-1">
                    <li>永久删除您的所有个人信息</li>
                    <li>无法恢复您的账户数据</li>
                    <li>需要重新注册才能再次使用本服务</li>
                  </ul>
                </div>
              </div>
            </div>
          </div>

          <div class="flex justify-between space-x-4">
            <button
              type="button"
              @click="cancelDeletion"
              class="flex w-1/2 justify-center rounded-md bg-gray-500 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-gray-600 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-gray-600"
            >
              取消
            </button>
            <button
              type="button"
              @click="firstConfirm"
              class="flex w-1/2 justify-center rounded-md bg-red-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-red-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-red-600"
            >
              我了解风险，继续注销
            </button>
          </div>
        </div>

        <!-- 第二步确认 -->
        <div v-if="step === 2" class="space-y-6">
          <div class="rounded-md bg-red-50 p-4">
            <div class="flex">
              <div class="ml-3">
                <h3 class="text-sm font-medium text-red-800">最终警告</h3>
                <div class="mt-2 text-sm text-red-700">
                  <p>请再次确认您要注销账户：</p>
                  <ul class="mt-2 list-disc pl-5 space-y-1">
                    <li>此操作<strong>不可撤销</strong></li>
                    <li>所有与您账户相关的数据将被删除</li>
                    <li>您将立即退出登录状态</li>
                  </ul>
                </div>
              </div>
            </div>
          </div>

          <div class="flex justify-between space-x-4">
            <button
              type="button"
              @click="cancelDeletion"
              class="flex w-1/2 justify-center rounded-md bg-gray-500 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-gray-600 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-gray-600"
            >
              取消
            </button>
            <button
              type="button"
              @click="secondConfirm"
              :disabled="countdownActive"
              class="flex w-1/2 justify-center rounded-md bg-red-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-red-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-red-600 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {{ countdownActive ? `请等待 ${countdown} 秒` : '是的，我确认继续' }}
            </button>
          </div>
        </div>

        <!-- 第三步最终确认 -->
        <div v-if="step === 3" class="space-y-6">
          <div class="rounded-md bg-red-50 p-4">
            <div class="flex">
              <div class="ml-3">
                <h3 class="text-sm font-medium text-red-800">最终确认</h3>
                <div class="mt-2 text-sm text-red-700">
                  <p>这是最后的确认步骤。一旦确认，您的账户将被永久删除。</p>
                  <p class="mt-2 font-bold">此操作无法撤销！</p>
                </div>
              </div>
            </div>
          </div>

          <div class="flex justify-between space-x-4">
            <button
              type="button"
              @click="cancelDeletion"
              :disabled="isProcessing"
              class="flex w-1/2 justify-center rounded-md bg-gray-500 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-gray-600 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-gray-600 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              我改变主意了
            </button>
            <button
              type="button"
              @click="finalConfirm"
              :disabled="countdownActive || isProcessing"
              class="flex w-1/2 justify-center rounded-md bg-red-600 px-3 py-1.5 text-sm font-semibold leading-6 text-white shadow-sm hover:bg-red-500 focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-red-600 disabled:opacity-50 disabled:cursor-not-allowed"
            >
              <span v-if="isProcessing">处理中...</span>
              <span v-else-if="countdownActive">请等待 {{ countdown }} 秒</span>
              <span v-else>确认注销我的账户</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
