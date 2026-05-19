<template>
  <div class="login">
    <el-card class="login-card">
      <h2 style="text-align:center;margin-bottom:24px">Xioayi Admin</h2>
      <el-form :model="form" @keyup.enter="handleLogin">
        <el-form-item><el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" size="large" /></el-form-item>
        <el-form-item><el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" size="large" show-password /></el-form-item>
        <el-form-item><el-button type="primary" size="large" style="width:100%" @click="handleLogin" :loading="loading">登 录</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { useAuthStore } from '../../store/auth'

const authStore = useAuthStore()
const loading = ref(false)
const form = reactive({ username: 'admin', password: '123456' })
const handleLogin = async () => {
  loading.value = true
  try { await authStore.login(form) } catch {} finally { loading.value = false }
}
</script>

<style scoped>
.login { height: 100vh; display: flex; justify-content: center; align-items: center; background: #2d3a4b; }
.login-card { width: 420px; }
</style>
