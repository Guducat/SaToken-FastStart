<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router' // 导入 useRouter
import AppHeader from '../components/AppHeader.vue'

// 获取当前登录用户的用户名
const username = computed(() => localStorage.getItem('username') || '')

const router = useRouter() // 获取 router 实例

const goBack = () => {
  router.go(-1) // 返回上一页
}
</script>

<template>
  <div class="bg-white">
    <AppHeader />

    <div class="flex min-h-screen flex-col justify-center items-center px-6 py-12 lg:px-8">
      <div class="sm:mx-auto sm:w-full sm:max-w-md">
        <div class="bg-white shadow-lg rounded-lg overflow-hidden">
          <div class="bg-indigo-600 px-6 py-4">
            <h2 class="text-xl font-bold text-white text-center">管理员控制台</h2>
          </div>

          <div class="p-6">
            <div class="flex items-center justify-center space-x-2 bg-green-100 text-green-800 p-4 rounded-lg">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span class="text-lg font-medium">您是管理员</span>
            </div>

            <div class="mt-4 text-center text-gray-600">
              <p>用户名: {{ username }}</p>
            </div>

            <div class="mt-6 flex justify-center space-x-4">
              <router-link
                to="/"
                class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
              >
                返回首页
              </router-link>
              <button
                @click="goBack"
                class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500"
              >
                返回上一页
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
