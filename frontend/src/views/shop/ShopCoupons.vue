<template>
  <div>
    <h2>优惠券领取</h2>
    <el-row :gutter="16">
      <el-col :span="8" v-for="c in list" :key="c.id" style="margin-bottom:16px">
        <el-card :body-style="{padding:'20px'}">
          <div style="display:flex;align-items:center;gap:12px">
            <div style="width:80px;height:80px;background:linear-gradient(135deg,#f093fb,#f5576c);border-radius:8px;display:flex;align-items:center;justify-content:center;color:#fff;font-weight:bold;font-size:18px;flex-shrink:0">
              {{ c.type===0?c.value+'%':'¥'+c.value }}
            </div>
            <div style="flex:1">
              <h4 style="margin:0 0 4px">{{ c.name }}</h4>
              <p style="margin:0 0 8px;color:#999;font-size:13px">满¥{{ c.minAmount }}可用</p>
              <el-button type="danger" size="small" :disabled="c.status!==0" @click="grab(c.id)">{{ c.status!==0?'已失效':'立即领取' }}</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listCoupons, grabCoupon } from '../../api/coupon'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../store/auth'
import router from '../../router'

const authStore = useAuthStore()
const list = ref([])
onMounted(async () => { const r = await listCoupons({ size: 50 }); list.value = r.data.records })
const grab = async (id) => {
  if (!authStore.token) { router.push('/login'); return }
  try { await grabCoupon(id); ElMessage.success('领取成功') } catch {}
}
</script>
