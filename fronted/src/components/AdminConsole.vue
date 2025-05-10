<template>
  <div>
    <h2 class="text-2xl font-bold mb-4">用户管理</h2>
    <input type="text" v-model="search" placeholder="搜索用户" class="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline">
    <table class="table-auto w-full">
      <thead class="bg-gray-200">
        <tr>
          <th class="px-4 py-2">ID</th>
          <th class="px-4 py-2">用户名</th>
          <th class="px-4 py-2">邮箱</th>
          <th class="px-4 py-2">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in filteredUsers" :key="user.id">
          <td class="border px-4 py-2">{{ user.id }}</td>
          <td class="border px-4 py-2">{{ user.username }}</td>
          <td class="border px-4 py-2">{{ user.email }}</td>
          <td class="border px-4 py-2">
            <button @click="deleteUser(user.id)" class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">删除</button>
          </td>
        </tr>
      </tbody>
    </table>
    <div class="flex justify-center mt-4">
      <button @click="prevPage" :disabled="currentPage === 1" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded mr-2 disabled:opacity-50">上一页</button>
      <span>{{ currentPage }} / {{ totalPages }}</span>
      <button @click="nextPage" :disabled="currentPage === totalPages" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded ml-2 disabled:opacity-50">下一页</button>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';

export default {
  setup() {
    const users = ref([]);
    const search = ref('');
    const currentPage = ref(1);
    const pageSize = 10;
    const totalUsers = ref(0);

    const fetchUsers = async () => {
      try {
        const response = await axios.get('/admin/users');
        if (Array.isArray(response.data)) {
          users.value = response.data;
          totalUsers.value = users.value.length;
          // 如果获取用户后当前页码超出总页数，重置为第一页
          if (totalPages.value > 0 && currentPage.value > totalPages.value) {
            currentPage.value = 1;
          } else if (totalPages.value === 0) { // 如果没有用户，当前页也应为1
            currentPage.value = 1;
          }
        } else {
          console.error('获取用户失败: 响应数据不是一个数组', response.data);
          users.value = [];
          totalUsers.value = 0;
          currentPage.value = 1;
        }
      } catch (error) {
        console.error('获取用户失败:', error);
        users.value = [];
        totalUsers.value = 0;
        currentPage.value = 1;
      }
    };

    onMounted(() => {
      fetchUsers();
    });

    const filteredUsers = computed(() => {
      if (!Array.isArray(users.value) || users.value.length === 0) {
        return [];
      }
      const searchTerm = search.value.toLowerCase();
      return users.value.filter(user => {
        const usernameMatch = user.username && typeof user.username === 'string' && user.username.toLowerCase().includes(searchTerm);
        const emailMatch = user.email && typeof user.email === 'string' && user.email.toLowerCase().includes(searchTerm);
        return usernameMatch || emailMatch;
      }).slice((currentPage.value - 1) * pageSize, currentPage.value * pageSize);
    });

    const totalPages = computed(() => {
      if (totalUsers.value === 0) {
        return 1; // 即使没有用户，也显示 1 页
      }
      return Math.ceil(totalUsers.value / pageSize);
    });

    const prevPage = () => {
      if (currentPage.value > 1) {
        currentPage.value--;
      }
    };

    const nextPage = () => {
      if (currentPage.value < totalPages.value) {
        currentPage.value++;
      }
    };

    const deleteUser = async (id) => {
      try {
        await axios.delete(`/admin/users/${id}`);
        fetchUsers(); // 刷新用户列表
      } catch (error) {
        console.error('删除用户失败:', error);
      }
    };

    return {
      users,
      search,
      currentPage,
      pageSize,
      totalUsers,
      filteredUsers,
      totalPages,
      prevPage,
      nextPage,
      deleteUser
    };
  }
}
</script>

<style scoped>
table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  border: 1px solid #ddd;
  padding: 8px;
  text-align: left;
}

th {
  background-color: #f2f2f2;
}


.pagination button {
  padding: 5px 10px;
  margin: 0 5px;
}
</style>