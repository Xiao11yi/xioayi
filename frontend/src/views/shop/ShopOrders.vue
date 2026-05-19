<template>
  <div>
    <h2>我的订单</h2>
    <el-input v-model="searchNo" placeholder="输入订单号查询" style="width:300px;margin-bottom:16px" />
    <el-button type="primary" @click="search">查询</el-button>
    <el-table :data="orders" border stripe style="margin-top:12px">
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
    </el-table>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { getOrder } from '../../api/order'
import { ElMessage } from 'element-plus'

const searchNo = ref('')
const orders = ref([])
const search = async () => {
  if (!searchNo.value) return
  try { const r = await getOrder(searchNo.value); orders.value = [r.data] } catch { orders.value = [] }
}
</script>
