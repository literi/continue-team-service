<template>
  <div class="create-tenant">
    <h2>创建租户</h2>
    <form @submit.prevent="handleSubmit" class="tenant-form">
      <div class="form-group">
        <label for="name">租户名称</label>
        <input
          id="name"
          v-model="form.name"
          type="text"
          required
          placeholder="请输入租户名称"
        />
      </div>

      <div class="form-group">
        <label for="description">描述</label>
        <textarea
          id="description"
          v-model="form.description"
          placeholder="请输入租户描述"
          rows="4"
        ></textarea>
      </div>

      <div class="form-actions">
        <button type="button" @click="$router.back()" class="cancel-btn">取消</button>
        <button type="submit" :disabled="loading" class="submit-btn">
          {{ loading ? '创建中...' : '创建租户' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)

const form = ref({
  name: '',
  description: ''
})

const handleSubmit = async () => {
  if (!form.value.name.trim()) {
    alert('请输入租户名称')
    return
  }

  loading.value = true

  try {
    // Mock API call
    await new Promise(resolve => setTimeout(resolve, 1000))

    alert('租户创建成功！')
    router.push('/tenants')
  } catch (error) {
    console.error('创建租户失败:', error)
    alert('创建失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.create-tenant {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.tenant-form {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #409eff;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 32px;
}

.cancel-btn,
.submit-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.cancel-btn {
  background: #f5f5f5;
  color: #666;
}

.cancel-btn:hover {
  background: #e8e8e8;
}

.submit-btn {
  background: #409eff;
  color: white;
}

.submit-btn:hover:not(:disabled) {
  background: #337ecc;
}

.submit-btn:disabled {
  background: #a0cfff;
  cursor: not-allowed;
}
</style>