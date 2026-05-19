<template>
  <div>
    <el-card>
      <template #header>
        <span>商品管理</span>
        <el-button type="primary" size="small" style="float:right" @click="showDialog()">新增</el-button>
      </template>
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="price" label="价格" width="120">
          <template #default="{row}">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="showDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination layout="prev,pager,next" :total="total" :page-size="10" style="margin-top:16px;justify-content:center" @current-change="loadData" />
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑商品':'新增商品'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :precision="2" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" style="width:100%" /></el-form-item>
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
import { listProducts, createProduct, updateProduct, deleteProduct } from '../../api/product'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([]), total = ref(0), loading = ref(false)
const dialogVisible = ref(false), isEdit = ref(false)
const form = ref({ name: '', description: '', price: 0, stock: 0 })

const loadData = async (page = 1) => {
  loading.value = true
  try { const r = await listProducts({ page, size: 10 }); list.value = r.data.records; total.value = r.data.total } catch {} finally { loading.value = false }
}
const showDialog = (row) => {
  isEdit.value = !!row; dialogVisible.value = true
  form.value = row ? { ...row } : { name: '', description: '', price: 0, stock: 0 }
}
const handleSave = async () => {
  try {
    isEdit.value ? await updateProduct(form.value.id, form.value) : await createProduct(form.value)
    ElMessage.success('操作成功'); dialogVisible.value = false; loadData()
  } catch {}
}
const handleDelete = id => {
  ElMessageBox.confirm('确认删除?').then(async () => { await deleteProduct(id); ElMessage.success('已删除'); loadData() }).catch(() => {})
}
onMounted(() => loadData())
</script>
