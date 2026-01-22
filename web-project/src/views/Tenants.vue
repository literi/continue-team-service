<template>
  <div class="tenants-page">
    <div class="page-header">
      <div class="header-left">
        <h2>租户管理</h2>
        <p>管理系统中的所有租户</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showCreateDialog">
          <el-icon><Plus /></el-icon>
          创建租户
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="租户名称">
          <el-input
            v-model="filterForm.name"
            placeholder="请输入租户名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="活跃" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
            <el-option label="待审核" value="PENDING" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 租户列表 -->
    <el-card>
      <template #header>
        <div class="table-header">
          <span>租户列表</span>
          <div class="table-actions">
            <el-button size="small" @click="loadTenants">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="tenants"
        v-loading="loading"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="租户名称" min-width="150" />
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userCount" label="用户数" width="100" />
        <el-table-column prop="projectCount" label="项目数" width="100" />
        <el-table-column prop="createdAt" label="创建时间" width="120" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewTenant(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="editTenant(scope.row)">编辑</el-button>
            <el-dropdown @command="(command:any) => handleCommand(command, scope.row)">
              <el-button size="small">
                更多 <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="users">管理用户</el-dropdown-item>
                  <el-dropdown-item command="projects">管理项目</el-dropdown-item>
                  <el-dropdown-item command="config" divided>配置管理</el-dropdown-item>
                  <el-dropdown-item
                    command="delete"
                    :disabled="scope.row.status === 'ACTIVE'"
                  >
                    删除
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 创建租户对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="创建租户"
      width="500px"
    >
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-width="100px"
      >
        <el-form-item label="租户名称" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入租户名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="createForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入租户描述"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="createForm.status" placeholder="请选择状态">
            <el-option label="活跃" value="ACTIVE" />
            <el-option label="停用" value="INACTIVE" />
            <el-option label="待审核" value="PENDING" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCreate" :loading="creating">
            创建
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import {
  Plus,
  Refresh,
  ArrowDown
} from '@element-plus/icons-vue'

interface Tenant {
  id: number
  name: string
  status: string
  description: string
  createdAt: string
  userCount: number
  projectCount: number
}

const tenants = ref<Tenant[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedTenants = ref<Tenant[]>([])

const filterForm = reactive({
  name: '',
  status: ''
})

const createDialogVisible = ref(false)
const creating = ref(false)
const createFormRef = ref()
const createForm = reactive({
  name: '',
  description: '',
  status: 'ACTIVE'
})

const createRules = {
  name: [
    { required: true, message: '请输入租户名称', trigger: 'blur' },
    { min: 2, max: 50, message: '租户名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '描述不能超过200个字符', trigger: 'blur' }
  ]
}

const getStatusType = (status: string) => {
  const types: Record<string, string> = {
    ACTIVE: 'success',
    INACTIVE: 'danger',
    PENDING: 'warning'
  }
  return types[status] || 'info'
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    ACTIVE: '活跃',
    INACTIVE: '停用',
    PENDING: '待审核'
  }
  return texts[status] || status
}

const loadTenants = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/v1/tenants', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        ...filterForm
      }
    })
    tenants.value = response.data.data
    total.value = response.data.total || tenants.value.length
  } catch (error) {
    console.error('Failed to load tenants:', error)
    ElMessage.error('加载租户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadTenants()
}

const handleReset = () => {
  Object.assign(filterForm, {
    name: '',
    status: ''
  })
  handleSearch()
}

const handleSelectionChange = (selection: Tenant[]) => {
  selectedTenants.value = selection
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  loadTenants()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadTenants()
}

const showCreateDialog = () => {
  createDialogVisible.value = true
  Object.assign(createForm, {
    name: '',
    description: '',
    status: 'ACTIVE'
  })
}

const handleCreate = async () => {
  if (!createFormRef.value) return

  await createFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      creating.value = true
      try {
        await axios.post('/api/v1/tenants', createForm)
        ElMessage.success('创建租户成功')
        createDialogVisible.value = false
        loadTenants()
      } catch (error) {
        console.error('Failed to create tenant:', error)
        ElMessage.error('创建租户失败')
      } finally {
        creating.value = false
      }
    }
  })
}

const viewTenant = (tenant: Tenant) => {
  // 查看租户详情
  console.log('View tenant:', tenant)
}

const editTenant = (tenant: Tenant) => {
  // 编辑租户
  console.log('Edit tenant:', tenant)
}

const handleCommand = (command: string, tenant: Tenant) => {
  switch (command) {
    case 'users':
      // 管理用户
      console.log('Manage users for tenant:', tenant)
      break
    case 'projects':
      // 管理项目
      console.log('Manage projects for tenant:', tenant)
      break
    case 'config':
      // 配置管理
      console.log('Config for tenant:', tenant)
      break
    case 'delete':
      // 删除租户
      ElMessageBox.confirm(
        `确定要删除租户 "${tenant.name}" 吗？此操作不可恢复。`,
        '确认删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        // 执行删除逻辑
        console.log('Delete tenant:', tenant)
      })
      break
  }
}

onMounted(() => {
  loadTenants()
})
</script>

<style scoped>
.tenants-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-left h2 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.header-left p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.filter-card {
  margin-bottom: 20px;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-actions {
  display: flex;
  gap: 8px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>