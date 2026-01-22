<template>
  <div class="create-project">
    <div class="form-container">
      <h2>创建项目</h2>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        class="project-form"
      >
        <el-form-item label="项目名称" prop="name">
          <el-input
            v-model="form.name"
            placeholder="请输入项目名称"
            maxlength="50"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="项目描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入项目描述"
            :rows="4"
            maxlength="200"
            show-word-limit
          />
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

        <el-form-item label="项目状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="ACTIVE">活跃</el-radio>
            <el-radio label="ARCHIVED">归档</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitForm" :loading="loading">
            创建项目
          </el-button>
          <el-button @click="resetForm">重置</el-button>
          <router-link to="/projects" class="cancel-link">取消</router-link>
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
  name: '',
  description: '',
  tenantId: null as number | null,
  status: 'ACTIVE'
})

const rules = {
  name: [
    { required: true, message: '请输入项目名称', trigger: 'blur' },
    { min: 2, max: 50, message: '项目名称长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入项目描述', trigger: 'blur' },
    { min: 10, max: 200, message: '项目描述长度在 10 到 200 个字符', trigger: 'blur' }
  ],
  tenantId: [
    { required: true, message: '请选择所属租户', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择项目状态', trigger: 'change' }
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

    ElMessage.success('项目创建成功！')
    router.push('/projects')
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
.create-project {
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

.project-form {
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