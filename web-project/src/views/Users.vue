<template>
  <div class="users">
    <div class="page-header">
      <h2>用户管理</h2>
      <router-link to="/users/create" class="create-btn">
        <i class="icon-plus"></i>
        创建用户
      </router-link>
    </div>

    <div class="filters">
      <el-input
        v-model="searchQuery"
        placeholder="搜索用户名或邮箱"
        style="width: 300px"
        clearable
        @input="filterUsers"
      />
      <el-select
        v-model="statusFilter"
        placeholder="筛选状态"
        style="width: 120px"
        clearable
        @change="filterUsers"
      >
        <el-option label="活跃" value="ACTIVE" />
        <el-option label="禁用" value="DISABLED" />
      </el-select>
    </div>

    <el-table
      :data="filteredUsers"
      style="width: 100%"
      :default-sort="{prop: 'createdAt', order: 'descending'}"
    >
      <el-table-column prop="username" label="用户名" width="120" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="scope">
          <el-tag :type="getRoleTagType(scope.row.role)">
            {{ getRoleLabel(scope.row.role) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="tenantName" label="所属租户" width="120" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'danger'">
            {{ scope.row.status === 'ACTIVE' ? '活跃' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="160">
        <template #default="scope">
          {{ formatDate(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button
            size="small"
            @click="editUser(scope.row)"
            type="primary"
            plain
          >
            编辑
          </el-button>
          <el-button
            size="small"
            @click="toggleUserStatus(scope.row)"
            :type="scope.row.status === 'ACTIVE' ? 'warning' : 'success'"
            plain
          >
            {{ scope.row.status === 'ACTIVE' ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div v-if="filteredUsers.length === 0" class="empty-state">
      <p>{{ users.length === 0 ? '暂无用户数据' : '没有找到匹配的用户' }}</p>
      <router-link v-if="users.length === 0" to="/users/create" class="create-link">
        创建第一个用户
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface User {
  id: number
  username: string
  email: string
  role: string
  tenantName: string
  status: string
  createdAt: string
}

const users = ref<User[]>([])
const searchQuery = ref('')
const statusFilter = ref('')

const filteredUsers = computed(() => {
  let filtered = users.value

  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(user =>
      user.username.toLowerCase().includes(query) ||
      user.email.toLowerCase().includes(query)
    )
  }

  if (statusFilter.value) {
    filtered = filtered.filter(user => user.status === statusFilter.value)
  }

  return filtered
})

const loadUsers = async () => {
  // Mock data
  users.value = [
    {
      id: 1,
      username: 'admin',
      email: 'admin@example.com',
      role: 'ADMIN',
      tenantName: '系统租户',
      status: 'ACTIVE',
      createdAt: '2026-01-15T09:00:00Z'
    },
    {
      id: 2,
      username: 'developer',
      email: 'dev@example.com',
      role: 'DEVELOPER',
      tenantName: '研发部',
      status: 'ACTIVE',
      createdAt: '2026-01-16T10:30:00Z'
    },
    {
      id: 3,
      username: 'analyst',
      email: 'analyst@example.com',
      role: 'ANALYST',
      tenantName: '数据部',
      status: 'DISABLED',
      createdAt: '2026-01-17T14:20:00Z'
    }
  ]
}

const filterUsers = () => {
  // Filtering is handled by computed property
}

const editUser = (user: User) => {
  // TODO: 实现编辑功能
  console.log('Edit user:', user)
}

const toggleUserStatus = async (user: User) => {
  const action = user.status === 'ACTIVE' ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(
      `确定要${action}用户 "${user.username}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    // Mock API call
    user.status = user.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
    ElMessage.success(`用户${action}成功`)
  } catch {
    // User cancelled
  }
}

const getRoleTagType = (role: string) => {
  switch (role) {
    case 'ADMIN': return 'danger'
    case 'DEVELOPER': return 'primary'
    case 'ANALYST': return 'success'
    default: return ''
  }
}

const getRoleLabel = (role: string) => {
  switch (role) {
    case 'ADMIN': return '管理员'
    case 'DEVELOPER': return '开发者'
    case 'ANALYST': return '分析师'
    default: return role
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.users {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0;
  color: #333;
}

.create-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: #409eff;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  font-size: 14px;
  transition: background-color 0.3s;
}

.create-btn:hover {
  background: #337ecc;
}

.filters {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.create-link {
  color: #409eff;
  text-decoration: none;
  font-weight: 500;
}

.create-link:hover {
  text-decoration: underline;
}
</style>