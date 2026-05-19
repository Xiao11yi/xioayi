<template>
  <div>
    <h2>商品列表</h2>
    <el-row :gutter="16">
      <el-col :span="6" v-for="p in list" :key="p.id" style="margin-bottom:16px">
        <el-card :body-style="{padding:'16px'}">
          <div style="height:100px;background:linear-gradient(135deg,#667eea,#764ba2);border-radius:4px;margin-bottom:12px;display:flex;align-items:center;justify-content:center;color:#fff;font-size:24px">{{ p.name[0] }}</div>
          <h3 style="margin:0 0 4px">{{ p.name }}</h3>
          <p style="color:#999;font-size:13px;margin:0 0 8px">{{ p.description }}</p>
          <div style="display:flex;justify-content:space-between;align-items:center">
            <span style="color:#f56c6c;font-size:20px;font-weight:bold">¥{{ p.price }}</span>
            <el-button type="primary" size="small" @click="handleBuy(p)">立即购买</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-pagination layout="prev,pager,next" :total="total" :page-size="12" style="justify-content:center" @current-change="load" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listProducts } from '../../api/product'
import { createOrder, payOrder } from '../../api/order'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '../../store/auth'
import router from '../../router'

const authStore = useAuthStore()
const list = ref([]), total = ref(0)
const load = async (page = 1) => {
  const r = await listProducts({ page, size: 12 })
  list.value = r.data.records; total.value = r.data.total
}
const handleBuy = async (product) => {
  if (!authStore.token) { router.push('/login'); return }
  try {
    const r = await createOrder(product.id)
    const order = r.data
    ElMessage.success(`订单已创建: ${order.orderNo}`)
    ElMessageBox.confirm(`订单 ${order.orderNo} ¥${order.amount}，去支付?`, '提示').then(async () => {
      const formR = await payOrder(order.orderNo)
      const div = document.createElement('div')
      div.innerHTML = formR.data
      document.body.appendChild(div)
      div.querySelector('form').submit()
    }).catch(() => {})
  } catch {}
}
onMounted(load)
</script>
