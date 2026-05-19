<template>
  <el-container style="height:100vh">
    <el-aside width="200px" style="background:#304156">
      <div class="logo">Xioayi Admin</div>
      <el-menu :default-active="$route.path" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409eff">
        <el-menu-item index="/dashboard"><el-icon><DataBoard /></el-icon><span>仪表盘</span></el-menu-item>
        <el-menu-item index="/product"><el-icon><Goods /></el-icon><span>商品管理</span></el-menu-item>
        <el-menu-item index="/coupon"><el-icon><Ticket /></el-icon><span>优惠券管理</span></el-menu-item>
        <el-menu-item index="/order"><el-icon><List /></el-icon><span>订单管理</span></el-menu-item>
        <el-menu-item index="/user"><el-icon><User /></el-icon><span>用户管理</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:#fff;border-bottom:1px solid #e6e6e6;display:flex;align-items:center;justify-content:flex-end">
        <el-dropdown @command="handleCommand">
          <span style="cursor:pointer">{{ authStore.username }}<el-icon style="margin-left:4px"><ArrowDown /></el-icon></span>
          <template #dropdown>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </template>
        </el-dropdown>
      </el-header>
      <el-main style="background:#f0f2f5">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useAuthStore } from '../store/auth'
const authStore = useAuthStore()
const handleCommand = cmd => cmd === 'logout' && authStore.logout()
</script>

<style scoped>
.logo { height: 56px; line-height: 56px; text-align: center; color: #fff; font-size: 18px; font-weight: bold; }
</style>
