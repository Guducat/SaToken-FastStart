<script setup>
// 首页组件
import { ref, computed } from 'vue'
import { Dialog, DialogPanel } from '@headlessui/vue'
import { XMarkIcon } from '@heroicons/vue/24/outline'
import AppHeader from '../components/AppHeader.vue'

// 检查用户是否已登录
const isLoggedIn = computed(() => localStorage.getItem('token') !== null)
const isAdmin = computed(() => localStorage.getItem('isAdmin') === 'true')
const username = computed(() => localStorage.getItem('username') || '')

// 移动菜单状态
const mobileMenuOpen = ref(false)

// 导航菜单
const publicNavigation = [
  { name: '首页', href: '/' },
  { name: 'GitHub', href: 'https://github.com/Guducat/SaToken-FastStart', target: '_blank' },
]

const privateNavigation = [
  ...publicNavigation,
  { name: '更新日志', href: '#changelog' },
  { name: '社区互动', href: '#community' },
  { name: '提交 Issue', href: 'https://github.com/Guducat/SaToken-FastStart/issues', target: '_blank' },
]

// 根据登录状态选择导航菜单
const navigation = computed(() => isLoggedIn.value ? privateNavigation : publicNavigation)

// 退出登录
const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('isAdmin')
  window.location.reload()
}
</script>

<template>
  <div class="bg-white">
    <header class="absolute inset-x-0 top-0 z-50">
      <nav class="flex items-center justify-between p-6 lg:px-8" aria-label="Global">
        <div class="flex lg:flex-1">
          <a href="/" class="-m-1.5 p-1.5">
            <span class="sr-only">SaToken-Vue3</span>
            <img class="h-8 w-auto" src="/vite.svg" alt="Logo" />
          </a>
        </div>
        <div class="flex lg:hidden">
          <button type="button" class="-m-2.5 inline-flex items-center justify-center rounded-md p-2.5 text-gray-700" @click="mobileMenuOpen = true">
            <span class="sr-only">打开菜单</span>
            <svg class="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" aria-hidden="true">
              <path stroke-linecap="round" stroke-linejoin="round" d="M3.75 6.75h16.5M3.75 12h16.5m-16.5 5.25h16.5" />
            </svg>
          </button>
        </div>
        <div class="hidden lg:flex lg:gap-x-12">
          <a v-for="item in navigation" :key="item.name" :href="item.href" :target="item.target || '_self'" class="text-sm font-semibold leading-6 text-gray-900">
            {{ item.name }}
          </a>
        </div>
        <div class="hidden lg:flex lg:flex-1 lg:justify-end lg:gap-x-6">
          <template v-if="isLoggedIn">
            <a href="/user" class="text-sm font-semibold leading-6 text-gray-900">个人中心</a>
            <a v-if="isAdmin" href="/admin" class="text-sm font-semibold leading-6 text-gray-900">管理控制台</a>
            <button @click="logout" class="text-sm font-semibold leading-6 text-gray-900">
              退出登录 ({{ username }})
            </button>
          </template>
          <template v-else>
            <a href="/login" class="text-sm font-semibold leading-6 text-gray-900">登录</a>
            <a href="/register" class="text-sm font-semibold leading-6 text-indigo-600">注册 <span aria-hidden="true">&rarr;</span></a>
          </template>
        </div>
      </nav>
      <Dialog as="div" class="lg:hidden" @close="mobileMenuOpen = false" :open="mobileMenuOpen">
        <div class="fixed inset-0 z-50" />
        <DialogPanel class="fixed inset-y-0 right-0 z-50 w-full overflow-y-auto bg-white px-6 py-6 sm:max-w-sm sm:ring-1 sm:ring-gray-900/10">
          <div class="flex items-center justify-between">
            <a href="/" class="-m-1.5 p-1.5">
              <span class="sr-only">SaToken-Vue3</span>
              <img class="h-8 w-auto" src="/vite.svg" alt="Logo" />
            </a>
            <button type="button" class="-m-2.5 rounded-md p-2.5 text-gray-700" @click="mobileMenuOpen = false">
              <span class="sr-only">关闭菜单</span>
              <XMarkIcon class="h-6 w-6" aria-hidden="true" />
            </button>
          </div>
          <div class="mt-6 flow-root">
            <div class="-my-6 divide-y divide-gray-500/10">
              <div class="space-y-2 py-6">
                <a
                  v-for="item in navigation"
                  :key="item.name"
                  :href="item.href"
                  :target="item.target || '_self'"
                  class="-mx-3 block rounded-lg px-3 py-2 text-base font-semibold leading-7 text-gray-900 hover:bg-gray-50"
                  @click="mobileMenuOpen = false"
                >
                  {{ item.name }}
                </a>
              </div>
              <div class="py-6">
                <template v-if="isLoggedIn">
                  <a
                    href="/user"
                    class="-mx-3 block rounded-lg px-3 py-2.5 text-base font-semibold leading-7 text-gray-900 hover:bg-gray-50"
                    @click="mobileMenuOpen = false"
                  >
                    个人中心
                  </a>
                  <a
                    v-if="isAdmin"
                    href="/admin"
                    class="-mx-3 block rounded-lg px-3 py-2.5 text-base font-semibold leading-7 text-gray-900 hover:bg-gray-50"
                    @click="mobileMenuOpen = false"
                  >
                    管理控制台
                  </a>
                  <button
                    @click="() => { logout(); mobileMenuOpen = false; }"
                    class="-mx-3 block rounded-lg px-3 py-2.5 text-base font-semibold leading-7 text-gray-900 hover:bg-gray-50 w-full text-left"
                  >
                    退出登录 ({{ username }})
                  </button>
                </template>
                <template v-else>
                  <a
                    href="/login"
                    class="-mx-3 block rounded-lg px-3 py-2.5 text-base font-semibold leading-7 text-gray-900 hover:bg-gray-50"
                    @click="mobileMenuOpen = false"
                  >
                    登录
                  </a>
                  <a
                    href="/register"
                    class="-mx-3 block rounded-lg px-3 py-2.5 text-base font-semibold leading-7 text-indigo-600 hover:bg-gray-50"
                    @click="mobileMenuOpen = false"
                  >
                    注册
                  </a>
                </template>
              </div>
            </div>
          </div>
        </DialogPanel>
      </Dialog>
    </header>

    <div class="relative isolate px-6 pt-14 lg:px-8">
      <div class="absolute inset-x-0 -top-40 -z-10 transform-gpu overflow-hidden blur-3xl sm:-top-80" aria-hidden="true">
        <div class="relative left-[calc(50%-11rem)] aspect-1155/678 w-[36.125rem] -translate-x-1/2 rotate-[30deg] bg-linear-to-tr from-[#ff80b5] to-[#9089fc] opacity-30 sm:left-[calc(50%-30rem)] sm:w-[72.1875rem]" style="clip-path: polygon(74.1% 44.1%, 100% 61.6%, 97.5% 26.9%, 85.5% 0.1%, 80.7% 2%, 72.5% 32.5%, 60.2% 62.4%, 52.4% 68.1%, 47.5% 58.3%, 45.2% 34.5%, 27.5% 76.7%, 0.1% 64.9%, 17.9% 100%, 27.6% 76.8%, 76.1% 97.7%, 74.1% 44.1%)" />
      </div>
      <div class="mx-auto max-w-2xl py-32 sm:py-48 lg:py-56">
        <div class="hidden sm:mb-8 sm:flex sm:justify-center">
          <div class="relative rounded-full px-3 py-1 text-sm/6 text-gray-600 ring-1 ring-gray-900/10 hover:ring-gray-900/20">
            项目现已开源 <a href="#" class="font-semibold text-indigo-600"><span class="absolute inset-0" aria-hidden="true" />了解更多 <span aria-hidden="true">&rarr;</span></a>
          </div>
        </div>
        <div class="text-center">
          <h1 class="text-5xl font-semibold tracking-tight text-balance text-gray-900 sm:text-7xl">Sa-token脚手架</h1>
          <p class="mt-8 text-lg font-medium text-pretty text-gray-500 sm:text-xl/8">SpringBoot+Vue3+Sa-Token+TailwindCSS，<br>致力于提供一个快速启动的前后端分离项目模板</p>
          <div class="mt-10 flex items-center justify-center gap-x-6">
            <a href="#" class="rounded-md bg-indigo-600 px-3.5 py-2.5 text-sm font-semibold text-white shadow-xs hover:bg-indigo-500 focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-indigo-600">开始使用</a>
            <a href="#" class="text-sm/6 font-semibold text-gray-900">了解更多 <span aria-hidden="true">→</span></a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


