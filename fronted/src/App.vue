<script setup>
import AppHeader from './components/AppHeader.vue'
import AppFooter from './components/AppFooter.vue'
</script>

<template>
  <div class="min-h-screen bg-gray-50">
    <router-view />
    <AppFooter />
  </div>
</template>

<style>
/* 全局样式 */
body {
  margin: 0;
  min-height: 100vh;
  background-color: #f3f4f6;
}
</style>