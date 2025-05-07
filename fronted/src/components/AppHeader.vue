<script setup>
import { ref, computed } from 'vue'
import { Dialog, DialogPanel } from '@headlessui/vue'
import { Bars3Icon, XMarkIcon } from '@heroicons/vue/24/outline'
import { useRouter } from 'vue-router'

const router = useRouter()

// 检查用户是否已登录
const isLoggedIn = computed(() => localStorage.getItem('token') !== null)
const username = computed(() => localStorage.getItem('username') || '')
const isAdmin = computed(() => localStorage.getItem('isAdmin') === 'true')

// 导航菜单
const publicNavigation = [
  { name: '首页', href: '/' },
  { name: 'GitHub', href: 'https://github.com/Guducat/SaToken-Vue3', target: '_blank' },
]

const privateNavigation = [
  ...publicNavigation,
  { name: '更新日志', href: '#changelog' },
  { name: '社区互动', href: '#community' },
  { name: '提交 Issue', href: 'https://github.com/Guducat/SaToken-Vue3/issues', target: '_blank' },
]

// 根据登录状态选择导航菜单
const navigation = computed(() => isLoggedIn.value ? privateNavigation : publicNavigation)

// 退出登录
const logout = async () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('isAdmin')

  // 跳转到首页
  router.push('/')
}

const mobileMenuOpen = ref(false)
</script>

<template>
  <header class="absolute inset-x-0 top-0 z-50">
    <nav class="flex items-center justify-between p-6 lg:px-8" aria-label="Global">
      <div class="flex lg:flex-1">
        <router-link to="/" class="-m-1.5 p-1.5">
          <span class="sr-only">SaToken-Vue3</span>
          <img class="h-8 w-auto" src="/vite.svg" alt="Logo" />
        </router-link>
      </div>
      <div class="flex lg:hidden">
        <button type="button" class="-m-2.5 inline-flex items-center justify-center rounded-md p-2.5 text-gray-700" @click="mobileMenuOpen = true">
          <span class="sr-only">打开菜单</span>
          <Bars3Icon class="size-6" aria-hidden="true" />
        </button>
      </div>
      <div class="hidden lg:flex lg:gap-x-12">
        <template v-for="item in navigation" :key="item.name">
          <!-- 外部链接使用普通的a标签 -->
          <a v-if="item.target" :href="item.href" :target="item.target" class="text-sm/6 font-semibold text-gray-900">
            {{ item.name }}
          </a>
          <!-- 内部链接使用router-link -->
          <router-link v-else :to="item.href" class="text-sm/6 font-semibold text-gray-900">
            {{ item.name }}
          </router-link>
        </template>
      </div>
      <div class="hidden lg:flex lg:flex-1 lg:justify-end lg:gap-x-6">
        <template v-if="isLoggedIn">
          <router-link to="/user" class="text-sm/6 font-semibold text-gray-900">个人中心</router-link>
          <router-link v-if="isAdmin" to="/admin" class="text-sm/6 font-semibold text-gray-900">管理控制台</router-link>
          <button @click="logout" class="text-sm/6 font-semibold text-gray-900">
            退出登录 ({{ username }})
          </button>
        </template>
        <template v-else>
          <router-link to="/login" class="text-sm/6 font-semibold text-gray-900">登录</router-link>
          <router-link to="/register" class="text-sm/6 font-semibold text-indigo-600">注册 <span aria-hidden="true">&rarr;</span></router-link>
        </template>
      </div>
    </nav>
    <Dialog class="lg:hidden" @close="mobileMenuOpen = false" :open="mobileMenuOpen">
      <div class="fixed inset-0 z-50" />
      <DialogPanel class="fixed inset-y-0 right-0 z-50 w-full overflow-y-auto bg-white px-6 py-6 sm:max-w-sm sm:ring-1 sm:ring-gray-900/10">
        <div class="flex items-center justify-between">
          <router-link to="/" class="-m-1.5 p-1.5">
            <span class="sr-only">SaToken-Vue3</span>
            <img class="h-8 w-auto" src="/vite.svg" alt="Logo" />
          </router-link>
          <button type="button" class="-m-2.5 rounded-md p-2.5 text-gray-700" @click="mobileMenuOpen = false">
            <span class="sr-only">关闭菜单</span>
            <XMarkIcon class="size-6" aria-hidden="true" />
          </button>
        </div>
        <div class="mt-6 flow-root">
          <div class="-my-6 divide-y divide-gray-500/10">
            <div class="space-y-2 py-6">
              <template v-for="item in navigation" :key="item.name">
                <!-- 外部链接使用普通的a标签 -->
                <a
                  v-if="item.target"
                  :href="item.href"
                  :target="item.target"
                  class="-mx-3 block rounded-lg px-3 py-2 text-base/7 font-semibold text-gray-900 hover:bg-gray-50"
                  @click="mobileMenuOpen = false"
                >
                  {{ item.name }}
                </a>
                <!-- 内部链接使用router-link -->
                <router-link
                  v-else
                  :to="item.href"
                  class="-mx-3 block rounded-lg px-3 py-2 text-base/7 font-semibold text-gray-900 hover:bg-gray-50"
                  @click="mobileMenuOpen = false"
                >
                  {{ item.name }}
                </router-link>
              </template>
            </div>
            <div class="py-6">
              <template v-if="isLoggedIn">
                <router-link
                  to="/user"
                  class="-mx-3 block rounded-lg px-3 py-2.5 text-base/7 font-semibold text-gray-900 hover:bg-gray-50"
                  @click="mobileMenuOpen = false"
                >
                  个人中心
                </router-link>
                <router-link
                  v-if="isAdmin"
                  to="/admin"
                  class="-mx-3 block rounded-lg px-3 py-2.5 text-base/7 font-semibold text-gray-900 hover:bg-gray-50"
                  @click="mobileMenuOpen = false"
                >
                  管理控制台
                </router-link>
                <button
                  @click="() => { logout(); mobileMenuOpen = false; }"
                  class="-mx-3 block rounded-lg px-3 py-2.5 text-base/7 font-semibold text-gray-900 hover:bg-gray-50 w-full text-left"
                >
                  退出登录 ({{ username }})
                </button>
              </template>
              <template v-else>
                <router-link
                  to="/login"
                  class="-mx-3 block rounded-lg px-3 py-2.5 text-base/7 font-semibold text-gray-900 hover:bg-gray-50"
                  @click="mobileMenuOpen = false"
                >
                  登录
                </router-link>
                <router-link
                  to="/register"
                  class="-mx-3 block rounded-lg px-3 py-2.5 text-base/7 font-semibold text-indigo-600 hover:bg-gray-50"
                  @click="mobileMenuOpen = false"
                >
                  注册
                </router-link>
              </template>
            </div>
          </div>
        </div>
      </DialogPanel>
    </Dialog>
  </header>
</template>
