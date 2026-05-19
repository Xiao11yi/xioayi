<template>
  <el-card>
    <template #header><span>用户管理</span></template>
    <el-table :data="users" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="enabled" label="状态" width="80">
        <template #default="{row}">
          <el-tag :type="row.enabled?'success':'info'">{{ row.enabled?'启用':'禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{row}">
          <el-button size="small" :type="row.enabled?'danger':'success'" @click="toggleStatus(row)">
            {{ row.enabled?'禁用':'启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../utils/request'
import { ElMessage } from 'element-plus'

const users = ref([])

const loadUsers = async () => {
  try {
    const r = await request.get('/users')
    users.value = r.data || []
  } catch {}
}

const toggleStatus = async (row) => {
  try {
    await request.put(`/users/${row.id}`, { enabled: !row.enabled })
    ElMessage.success('操作成功')
    loadUsers()
  } catch {}
}

onMounted(loadUsers)
</script>
