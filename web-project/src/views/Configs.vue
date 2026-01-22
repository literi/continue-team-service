<template>
  <div class="configs-page">
    <div class="page-header">
      <div class="header-left">
        <h2>配置管理</h2>
        <p>管理系统配置项</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="showCreateDialog">
          <el-icon><Plus /></el-icon>
          添加配置
        </el-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="filter-card">
      <el-form :inline="true" :model="filterForm" class="filter-form">
        <el-form-item label="配置键">
          <el-input
            v-model="filterForm.key"
            placeholder="请输入配置键"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="租户">
          <el-select
            v-model="filterForm.tenantId"
            placeholder="请选择租户"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="tenant in tenants"
              :key="tenant.id"
              :label="tenant.name"
              :value="tenant.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="作用域">
          <el-select v-model="filterForm.scope" placeholder="请选择作用域" clearable style="width: 120px">
            <el-option label="全局" value="GLOBAL" />
            <el-option label="租户" value="TENANT" />
            <el-option label="用户" value="USER" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 配置列表 -->
    <el-card>
      <template #header>
        <div class="table-header">
          <span>配置列表</span>
          <div class="table-actions">
            <el-button size="small" @click="loadConfigs">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="configs"
        v-loading="loading"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="key" label="配置键" min-width="150" />
        <el-table-column prop="value" label="配置值" min-width="200">
          <template #default="scope">
            <span :title="scope.row.value">{{ truncateText(scope.row.value, 50) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="scope" label="作用域" width="100">
          <template #default="scope">
            <el-tag :type="getScopeType(scope.row.scope)">
              {{ getScopeText(scope.row.scope) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="tenantId" label="租户" width="120">
          <template #default="scope">
            {{ getTenantName(scope.row.tenantId) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'danger'">
              {{ scope.row.status === 'ACTIVE' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="version" label="版本" width="80" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewConfig(scope.row)">查看</el-button>
            <el-button size="small" type="primary" @click="editConfig(scope.row)">编辑</el-button>
            <el-dropdown @command="(command) => handleCommand(command, scope.row)">
              <el-button size="small">
                更多 <el-icon class="el-icon--right"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="history">历史版本</el-dropdown-item>
                  <el-dropdown-item
                    command="toggle"
                    :disabled="!canToggleStatus(scope.row)"
                  >
                    {{ scope.row.status === 'ACTIVE' ? '禁用' : '启用' }}
                  </el-dropdown-item>
                  <el-dropdown-item command="delete" divided>
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

    <!-- 创建配置对话框 -->
    <el-dialog
      v-model="createDialogVisible"
      title="添加配置"
      width="600px"
    >
      <el-form
        ref="createFormRef"
        :model="createForm"
        :rules="createRules"
        label-width="100px"
      >
        <el-form-item label="配置键" prop="key">
          <el-input v-model="createForm.key" placeholder="请输入配置键" />
        </el-form-item>
        <el-form-item label="配置值" prop="value">
          <el-input
            v-model="createForm.value"
            type="textarea"
            :rows="3"
            placeholder="请输入配置值"
          />
        </el-form-item>
        <el-form-item label="作用域" prop="scope">
          <el-select v-model="createForm.scope" placeholder="请选择作用域">
            <el-option label="全局" value="GLOBAL" />
            <el-option label="租户" value="TENANT" />
            <el-option label="用户" value="USER" />
          </el-select>
        </el-form-item>
        <el-form-item
          v-if="createForm.scope === 'TENANT'"
          label="租户"
          prop="tenantId"
        >
          <el-select v-model="createForm.tenantId" placeholder="请选择租户">
            <el-option
              v-for="tenant in tenants"
              :key="tenant.id"
              :label="tenant.name"
              :value="tenant.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="createForm.description"
            type="textarea"
            :rows="2"
            placeholder="请输入配置描述"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCreate" :loading="creating">
            添加
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import {
  Plus,
  Refresh,
  ArrowDown
} from '@element-plus/icons-vue'

interface Config {
  id: number
  tenantId?: number
  key: string
  value: string
  scope: string
  version: number
  status: string
  description?: string
}

interface Tenant {
  id: number
  name: string
}

const configs = ref<Config[]>([])
const tenants = ref<Tenant[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const selectedConfigs = ref<Config[]>([])

const filterForm = reactive({
  key: '',
  tenantId: null,
  scope: ''
})

const createDialogVisible = ref(false)
const creating = ref(false)
const createFormRef = ref()
const createForm = reactive({
  key: '',
  value: '',
  scope: 'GLOBAL',
  tenantId: null,
  description: ''
})

const createRules = {
  key: [
    { required: true, message: '请输入配置键', trigger: 'blur' },
    { pattern: /^[a-zA-Z_][a-zA-Z0-9_]*$/, message: '配置键格式不正确', trigger: 'blur' }
  ],
  value: [
    { required: true, message: '请输入配置值', trigger: 'blur' }
  ],
  scope: [
    { required: true, message: '请选择作用域', trigger: 'change' }
  ],
  tenantId: [
    { required: true, message: '请选择租户', trigger: 'change' }
  ]
}

const getScopeType = (scope: string) => {
  const types: Record<string, string> = {
    GLOBAL: 'danger',
    TENANT: 'primary',
    USER: 'success'
  }
  return types[scope] || 'info'
}

const getScopeText = (scope: string) => {
  const texts: Record<string, string> = {
    GLOBAL: '全局',
    TENANT: '租户',
    USER: '用户'
  }
  return texts[scope] || scope
}

const getTenantName = (tenantId?: number) => {
  if (!tenantId) return '全局'
  const tenant = tenants.value.find(t => t.id === tenantId)
  return tenant ? tenant.name : `租户${tenantId}`
}

const truncateText = (text: string, maxLength: number) => {
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

const canToggleStatus = (config: Config) => {
  // 某些系统配置不允许禁用
  const systemKeys = ['system.version', 'system.maintenance']
  return !systemKeys.includes(config.key)
}

const loadConfigs = async () => {
  loading.value = true
  try {
    const response = await axios.get('/api/v1/configs', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        ...filterForm
      }
    })
    configs.value = response.data.data
    total.value = response.data.total || configs.value.length
  } catch (error) {
    console.error('Failed to load configs:', error)
    ElMessage.error('加载配置列表失败')
  } finally {
    loading.value = false
  }
}

const loadTenants = async () => {
  try {
    const response = await axios.get('/api/v1/tenants')
    tenants.value = response.data.data
  } catch (error) {
    console.error('Failed to load tenants:', error)
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadConfigs()
}

const handleReset = () => {
  Object.assign(filterForm, {
    key: '',
    tenantId: null,
    scope: ''
  })
  handleSearch()
}

const handleSelectionChange = (selection: Config[]) => {
  selectedConfigs.value = selection
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  loadConfigs()
}

const handleCurrentChange = (page: number) => {
  currentPage.value = page
  loadConfigs()
}

const showCreateDialog = () => {
  createDialogVisible.value = true
  Object.assign(createForm, {
    key: '',
    value: '',
    scope: 'GLOBAL',
    tenantId: null,
    description: ''
  })
}

const handleCreate = async () => {
  if (!createFormRef.value) return

  await createFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      creating.value = true
      try {
        await axios.post('/api/v1/configs', createForm)
        ElMessage.success('添加配置成功')
        createDialogVisible.value = false
        loadConfigs()
      } catch (error) {
        console.error('Failed to create config:', error)
        ElMessage.error('添加配置失败')
      } finally {
        creating.value = false
      }
    }
  })
}

const viewConfig = (config: Config) => {
  // 查看配置详情
  console.log('View config:', config)
}

const editConfig = (config: Config) => {
  // 编辑配置
  console.log('Edit config:', config)
}

const handleCommand = (command: string, config: Config) => {
  switch (command) {
    case 'history':
      // 查看历史版本
      console.log('View history for config:', config)
      break
    case 'toggle':
      // 切换状态
      const newStatus = config.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
      console.log('Toggle status for config:', config, 'to', newStatus)
      break
    case 'delete':
      // 删除配置
      ElMessageBox.confirm(
        `确定要删除配置 "${config.key}" 吗？此操作不可恢复。`,
        '确认删除',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        // 执行删除逻辑
        console.log('Delete config:', config)
      })
      break
  }
}

onMounted(() => {
  loadConfigs()
  loadTenants()
})
</script>

<style scoped>
.configs-page {
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