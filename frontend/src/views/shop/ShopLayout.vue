<template>
  <div>
    <el-menu mode="horizontal" :ellipsis="false" style="padding:0 40px">
      <el-menu-item><strong style="font-size:18px">🛒 Xioayi 商城</strong></el-menu-item>
      <el-menu-item index="1" @click="$router.push('/shop/products')">商品</el-menu-item>
      <el-menu-item index="2" @click="$router.push('/shop/coupons')">优惠券</el-menu-item>
      <el-menu-item index="3" @click="$router.push('/shop/orders')">我的订单</el-menu-item>
      <div style="flex:1" />
      <el-menu-item v-if="!authStore.token" @click="$router.push('/login')" style="border:0">登录</el-menu-item>
      <el-dropdown v-else @command="handleCmd" style="display:flex;align-items:center;padding:0 20px;cursor:pointer">
        <span>{{ authStore.username }}<el-icon style="margin-left:4px"><ArrowDown /></el-icon></span>
        <template #dropdown>
          <el-dropdown-item command="logout">退出</el-dropdown-item>
        </template>
      </el-dropdown>
    </el-menu>
    <div style="max-width:1200px;margin:0 auto;padding:20px">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '../../store/auth'
import router from '../../router'
const authStore = useAuthStore()
const handleCmd = cmd => cmd === 'logout' && authStore.logout()
</script>
