<template>
  <div>
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="6" v-for="item in stats" :key="item.label">
        <el-card><div class="stat">{{ item.label }}<br/><strong>{{ item.value }}</strong></div></el-card>
      </el-col>
    </el-row>
    <el-card>
      <template #header><span>快速操作</span></template>
      <el-space>
        <el-button type="primary" @click="$router.push('/product')">商品管理</el-button>
        <el-button type="success" @click="$router.push('/coupon')">优惠券管理</el-button>
        <el-button type="warning" @click="$router.push('/order')">订单管理</el-button>
        <el-button @click="$router.push('/user')">用户管理</el-button>
      </el-space>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listProducts } from '../../api/product'
import { listCoupons } from '../../api/coupon'
import { useAuthStore } from '../../store/auth'

const authStore = useAuthStore()
const stats = ref([
  { label: '欢迎', value: authStore.username },
  { label: '商品', value: '-' },
  { label: '优惠券', value: '-' },
  { label: '系统', value: '运行中' }
])

onMounted(async () => {
  try {
    const p = await listProducts()
    const c = await listCoupons()
    stats.value[1].value = p.data.total || 0
    stats.value[2].value = c.data.total || 0
  } catch {}
})
</script>

<style scoped>
.stat { font-size: 14px; color: #666; }
.stat strong { font-size: 24px; color: #333; display: block; margin-top: 8px; }
</style>
