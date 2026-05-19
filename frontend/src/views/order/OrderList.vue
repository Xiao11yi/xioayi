<template>
  <el-card>
    <template #header><span>订单管理（按订单号查询）</span></template>
    <div style="margin-bottom:16px;display:flex;gap:8px">
      <el-input v-model="searchNo" placeholder="输入订单号" style="width:300px" />
      <el-button type="primary" @click="searchOrder">查询</el-button>
    </div>
    <el-table :data="orders" border stripe>
      <el-table-column prop="orderNo" label="订单号" width="210" />
      <el-table-column prop="productName" label="商品" />
      <el-table-column prop="amount" label="金额" width="100">
        <template #default="{row}">¥{{ row.amount }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{row}">
          <el-tag :type="{0:'warning',1:'success',2:'info'}[row.status]">{{ {0:'待支付',1:'已支付',2:'已过期'}[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="alipayTradeNo" label="支付宝交易号" width="210" />
      <el-table-column prop="payTime" label="支付时间" width="170" />
      <el-table-column prop="createTime" label="创建时间" width="170" />
      <el-table-column label="过期时间" width="170">
        <template #default="{row}">{{ row.expireTime||'-' }}</template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import { getOrder } from '../../api/order'
import { ElMessage } from 'element-plus'

const searchNo = ref('')
const orders = ref([])

const searchOrder = async () => {
  if (!searchNo.value) { ElMessage.warning('请输入订单号'); return }
  try {
    const r = await getOrder(searchNo.value)
    orders.value = [r.data]
  } catch {
    orders.value = []
  }
}
</script>
