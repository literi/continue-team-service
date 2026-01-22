<template>
  <div class="create-user">
    <div class="form-container">
      <h2>创建用户</h2>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="user-form"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            maxlength="20"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input
            v-model="form.email"
            type="email"
            placeholder="请输入邮箱地址"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            show-password
          />
        </el-form-item>

        <el-form-item label="角色" prop="role">
          <el-select
            v-model="form.role"
            placeholder="请选择用户角色"
            style="width: 100%"
          >
            <el-option label="管理员" value="ADMIN" />
            <el-option label="开发者" value="DEVELOPER" />
            <el-option label="分析师" value="ANALYST" />
          </el-select>
        </el-form-item>

        <el-form-item label="所属租户" prop="tenantId">
          <el-select
            v-model="form.tenantId"
            placeholder="请选择所属租户"
            style="width: 100%"
          >
            <el-option
              v-for="tenant in tenants"
              :key="tenant.id"
              :label="tenant.name"
              :value="tenant.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="ACTIVE">活跃</el-radio>
            <el-radio label="DISABLED">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">
            创建用户
          </el-button>
          <el-button @click="resetForm">重置</el-button>
          <router-link to="/users" class="cancel-link">取消</router-link>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

interface Tenant {
  id: number
  name: string
}

const formRef = ref()
const loading = ref(false)
const tenants = ref<Tenant[]>([])

const form = reactive({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  role: '',
  tenantId: null as number | null,
  status: 'ACTIVE'
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: any) => {
        if (value !== form.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  role: [
    { required: true, message: '请选择用户角色', trigger: 'change' }
  ],
  tenantId: [
    { required: true, message: '请选择所属租户', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择用户状态', trigger: 'change' }
  ]
}

const loadTenants = async () => {
  // Mock data
  tenants.value = [
    { id: 1, name: '研发部' },
    { id: 2, name: '数据部' },
    { id: 3, name: '产品部' }
  ]
}

const submitForm = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    loading.value = true

    // Mock API call
    await new Promise(resolve => setTimeout(resolve, 1000))

    ElMessage.success('用户创建成功！')
    router.push('/users')
  } catch (error) {
    console.error('Validation failed:', error)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  formRef.value?.resetFields()
}

onMounted(() => {
  loadTenants()
})
</script>

<style scoped>
.create-user {
  padding: 20px;
  display: flex;
  justify-content: center;
}

.form-container {
  background: white;
  border-radius: 8px;
  padding: 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 600px;
}

.form-container h2 {
  margin: 0 0 24px 0;
  color: #333;
  text-align: center;
}

.user-form {
  max-width: none;
}

.cancel-link {
  margin-left: 16px;
  color: #666;
  text-decoration: none;
}

.cancel-link:hover {
  color: #409eff;
  text-decoration: underline;
}
</style>