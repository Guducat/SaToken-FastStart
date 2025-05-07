import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import api from './api' // 引入 API 服务

const app = createApp(App)

// 将 API 服务添加到全局属性
// 可以在组件中通过 this.$api 访问
app.config.globalProperties.$api = api

app.use(router).mount('#app')
