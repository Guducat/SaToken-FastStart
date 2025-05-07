<script setup>
import AppHeader from '../components/AppHeader.vue'
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api'

const router = useRouter()
const userInfo = reactive({
  id: '',
  username: '',
  nickname: '',
  email: '',
  avatarUrl: '',
  role: ''
})

// 编辑模式状态
const isEditing = ref(false)
// 编辑表单数据
const editForm = reactive({
  nickname: '',
  email: '',
  avatarUrl: ''
})
const errorMsg = ref('')
const successMsg = ref('')

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    const response = await api.getUserInfo()
    if (response.code === 200) {
      const data = response.data
      userInfo.id = data.id
      userInfo.username = data.username
      userInfo.nickname = data.nickname || ''
      userInfo.email = data.email || ''
      userInfo.avatarUrl = data.avatarUrl || ''
      userInfo.role = data.role || '普通用户'
    } else {
      console.error('Failed to fetch user info:', response.msg)
    }
  } catch (error) {
    console.error('Error fetching user info:', error)
  }
}

// 开始编辑用户信息
const startEditing = () => {
  editForm.nickname = userInfo.nickname
  editForm.email = userInfo.email
  editForm.avatarUrl = userInfo.avatarUrl
  isEditing.value = true
  errorMsg.value = ''
  successMsg.value = ''
}

// 取消编辑
const cancelEditing = () => {
  isEditing.value = false
  errorMsg.value = ''
  successMsg.value = ''
}

// 保存用户信息
const saveUserInfo = async () => {
  try {
    const response = await api.updateUserInfo({
      nickname: editForm.nickname,
      email: editForm.email,
      avatarUrl: editForm.avatarUrl
    })
    
    if (response.code === 200) {
      // 更新成功，刷新用户信息
      await fetchUserInfo()
      isEditing.value = false
      successMsg.value = '用户信息更新成功'
      setTimeout(() => {
        successMsg.value = ''
      }, 3000)
    } else {
      errorMsg.value = response.msg || '更新失败，请稍后再试'
    }
  } catch (error) {
    console.error('Error updating user info:', error)
    errorMsg.value = '更新失败，请稍后再试'
  }
}

onMounted(() => {
  fetchUserInfo()
})

const logout = async () => {
  // 清除本地存储的用户信息
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('isAdmin')
  
  // 跳转到首页
  router.push('/')
}
</script>

<template>
  <div class="bg-white">
    <AppHeader />
    
    <div class="flex min-h-screen flex-1 flex-col justify-center px-6 py-12 lg:px-8">
      <div class="sm:mx-auto sm:w-full sm:max-w-lg">
        <h2 class="mt-10 text-center text-2xl font-bold leading-9 tracking-tight text-gray-900">个人中心</h2>
      </div>

      <div class="mt-10 sm:mx-auto sm:w-full sm:max-w-lg">
        <!-- 成功消息 -->
        <div v-if="successMsg" class="mb-6 p-4 bg-green-50 border border-green-200 rounded-md">
          <p class="text-green-700">{{ successMsg }}</p>
        </div>

        <!-- 错误消息 -->
        <div v-if="errorMsg" class="mb-6 p-4 bg-red-50 border border-red-200 rounded-md">
          <p class="text-red-700">{{ errorMsg }}</p>
        </div>

        <!-- 查看模式 -->
        <div v-if="!isEditing" class="bg-white shadow overflow-hidden sm:rounded-lg">
          <div class="px-4 py-5 sm:px-6 flex justify-between items-center">
            <div>
              <h3 class="text-lg leading-6 font-medium text-gray-900">用户信息</h3>
              <p class="mt-1 max-w-2xl text-sm text-gray-500">个人账号详细信息</p>
            </div>
            <button 
              @click="startEditing" 
              class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              编辑信息
            </button>
          </div>
          <div class="border-t border-gray-200">
            <dl>
              <div class="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                <dt class="text-sm font-medium text-gray-500">用户名</dt>
                <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{{ userInfo.username }}</dd>
              </div>
              <div class="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                <dt class="text-sm font-medium text-gray-500">昵称</dt>
                <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{{ userInfo.nickname || '未设置' }}</dd>
              </div>
              <div class="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                <dt class="text-sm font-medium text-gray-500">邮箱</dt>
                <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">{{ userInfo.email || '未设置' }}</dd>
              </div>
              <div class="bg-white px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                <dt class="text-sm font-medium text-gray-500">角色</dt>
                <dd class="mt-1 text-sm sm:mt-0 sm:col-span-2">
                  <span 
                    class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium" 
                    :class="userInfo.role === 'admin' ? 'bg-purple-100 text-purple-800' : 'bg-blue-100 text-blue-800'"
                  >
                    {{ userInfo.role === 'admin' ? '管理员' : '普通用户' }}
                  </span>
                </dd>
              </div>
              <div class="bg-gray-50 px-4 py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
                <dt class="text-sm font-medium text-gray-500">头像</dt>
                <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                  <div v-if="userInfo.avatarUrl" class="flex items-center">
                    <img 
                      :src="userInfo.avatarUrl" 
                      alt="用户头像" 
                      class="h-16 w-16 rounded-full object-cover border-2 border-gray-200"
                    />
                    <span class="ml-3 text-xs text-gray-500 break-all">{{ userInfo.avatarUrl }}</span>
                  </div>
                  <span v-else>未设置</span>
                </dd>
              </div>
            </dl>
          </div>
          <div class="px-4 py-5 sm:px-6 flex justify-center space-x-4">
            <button 
              @click="logout" 
              class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
            >
              退出登录
            </button>
            <router-link 
              to="/" 
              class="inline-flex items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-md shadow-sm text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              返回首页
            </router-link>
          </div>
        </div>

        <!-- 编辑模式 -->
        <div v-else class="bg-white shadow overflow-hidden sm:rounded-lg">
          <div class="px-4 py-5 sm:px-6">
            <h3 class="text-lg leading-6 font-medium text-gray-900">编辑用户信息</h3>
            <p class="mt-1 max-w-2xl text-sm text-gray-500">修改您的个人信息</p>
          </div>
          <div class="border-t border-gray-200 px-4 py-5 sm:p-6">
            <form @submit.prevent="saveUserInfo" class="space-y-6">
              <div>
                <label for="nickname" class="block text-sm font-medium text-gray-700">昵称</label>
                <div class="mt-1">
                  <input 
                    id="nickname" 
                    name="nickname" 
                    type="text" 
                    v-model="editForm.nickname" 
                    placeholder="请输入昵称"
                    class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm py-2 px-3 border"
                  />
                </div>
              </div>

              <div>
                <label for="email" class="block text-sm font-medium text-gray-700">邮箱</label>
                <div class="mt-1">
                  <input 
                    id="email" 
                    name="email" 
                    type="email" 
                    v-model="editForm.email" 
                    placeholder="请输入邮箱"
                    class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm py-2 px-3 border"
                  />
                </div>
              </div>

              <div>
                <label for="avatarUrl" class="block text-sm font-medium text-gray-700">头像URL</label>
                <div class="mt-1">
                  <input 
                    id="avatarUrl" 
                    name="avatarUrl" 
                    type="url" 
                    v-model="editForm.avatarUrl" 
                    placeholder="请输入头像URL"
                    class="block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm py-2 px-3 border"
                  />
                </div>
              </div>

              <div class="flex justify-end space-x-3">
                <button 
                  type="button" 
                  @click="cancelEditing" 
                  class="inline-flex items-center px-4 py-2 border border-gray-300 shadow-sm text-sm font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                >
                  取消
                </button>
                <button 
                  type="submit" 
                  class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
                >
                  保存
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
