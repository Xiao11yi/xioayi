<template>
  <div>
    <el-card>
      <template #header>
        <span>优惠券管理</span>
        <el-button type="primary" size="small" style="float:right" @click="showDialog()">新增</el-button>
      </template>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" width="160" />
        <el-table-column prop="code" label="编码" width="100" />
        <el-table-column prop="type" label="类型" width="80">
          <template #default="{row}">{{ row.type===0?'百分比':'固定' }}</template>
        </el-table-column>
        <el-table-column prop="value" label="值" width="80">
          <template #default="{row}">{{ row.type===0? row.value+'%':'¥'+row.value }}</template>
        </el-table-column>
        <el-table-column prop="usageLimit" label="总量" width="70" />
        <el-table-column prop="usedCount" label="已用" width="70" />
        <el-table-column prop="status" label="状态" width="70">
          <template #default="{row}">{{ {0:'启用',1:'过期',2:'禁用'}[row.status] }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="showDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination layout="prev,pager,next" :total="total" :page-size="10" style="margin-top:16px;justify-content:center" @current-change="loadData" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑优惠券':'新增优惠券'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="form.type">
            <el-radio :label="0">百分比</el-radio>
            <el-radio :label="1">固定金额</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="折扣值"><el-input-number v-model="form.value" :precision="2" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="最低金额"><el-input-number v-model="form.minAmount" :precision="2" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="最高抵扣"><el-input-number v-model="form.maxAmount" :precision="2" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="总量限制"><el-input-number v-model="form.usageLimit" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="生效时间"><el-date-picker v-model="form.startTime" type="datetime" style="width:100%" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
        <el-form-item label="过期时间"><el-date-picker v-model="form.endTime" type="datetime" style="width:100%" value-format="YYYY-MM-DDTHH:mm:ss" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSave">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listCoupons, createCoupon, updateCoupon, deleteCoupon } from '../../api/coupon'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([]), total = ref(0), loading = ref(false)
const dialogVisible = ref(false), isEdit = ref(false)
const form = ref({ name: '', code: '', type: 0, value: 0, minAmount: 0, maxAmount: null, usageLimit: 0, startTime: '', endTime: '' })

const loadData = async (page = 1) => {
  loading.value = true
  try { const r = await listCoupons({ page, size: 10 }); list.value = r.data.records; total.value = r.data.total } catch {} finally { loading.value = false }
}
const showDialog = (row) => {
  isEdit.value = !!row; dialogVisible.value = true
  form.value = row ? { ...row } : { name: '', code: '', type: 0, value: 0, minAmount: 0, maxAmount: null, usageLimit: 0, startTime: '', endTime: '' }
}
const handleSave = async () => {
  try {
    isEdit.value ? await updateCoupon(form.value.id, form.value) : await createCoupon(form.value)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  } catch {}
}
const handleDelete = id => {
  ElMessageBox.confirm('确认删除?').then(async () => { await deleteProduct(id); ElMessage.success('已删除'); loadData() }).catch(() => {})
}
onMounted(() => loadData())
</script>
