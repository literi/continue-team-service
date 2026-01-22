<template>
  <div class="models">
    <div class="page-header">
      <h2>模型管理</h2>
      <button @click="refreshModels" class="refresh-btn">
        <i class="icon-refresh"></i>
        刷新
      </button>
    </div>

    <div class="models-grid">
      <div v-for="model in models" :key="model.id" class="model-card">
        <div class="model-header">
          <h3>{{ model.name }}</h3>
          <span :class="['status', model.status.toLowerCase()]">{{ model.status }}</span>
        </div>

        <div class="model-info">
          <div class="info-item">
            <span class="label">提供商:</span>
            <span class="value">{{ model.provider }}</span>
          </div>
          <div class="info-item">
            <span class="label">类型:</span>
            <span class="value">{{ getModelTypeLabel(model.type) }}</span>
          </div>
          <div class="info-item">
            <span class="label">上下文长度:</span>
            <span class="value">{{ model.contextLength }}</span>
          </div>
          <div class="info-item">
            <span class="label">支持功能:</span>
            <div class="capabilities">
              <el-tag
                v-for="cap in model.capabilities"
                :key="cap"
                size="small"
                type="info"
              >
                {{ getCapabilityLabel(cap) }}
              </el-tag>
            </div>
          </div>
        </div>

        <div class="model-stats">
          <div class="stat">
            <span class="stat-label">调用次数</span>
            <span class="stat-value">{{ model.callCount }}</span>
          </div>
          <div class="stat">
            <span class="stat-label">成功率</span>
            <span class="stat-value">{{ model.successRate }}%</span>
          </div>
          <div class="stat">
            <span class="stat-label">平均响应时间</span>
            <span class="stat-value">{{ model.avgResponseTime }}ms</span>
          </div>
        </div>

        <div class="model-actions">
          <button @click="testModel(model)" class="test-btn">测试</button>
          <button @click="configureModel(model)" class="config-btn">配置</button>
          <button
            @click="toggleModelStatus(model)"
            :class="['status-btn', model.status.toLowerCase()]"
          >
            {{ model.status === 'ACTIVE' ? '禁用' : '启用' }}
          </button>
        </div>
      </div>
    </div>

    <div v-if="models.length === 0" class="empty-state">
      <p>暂无模型数据</p>
      <p class="empty-desc">系统将自动发现和注册可用的AI模型</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

interface Model {
  id: number
  name: string
  provider: string
  type: string
  contextLength: number
  capabilities: string[]
  status: string
  callCount: number
  successRate: number
  avgResponseTime: number
}

const models = ref<Model[]>([])

const loadModels = async () => {
  // Mock data
  models.value = [
    {
      id: 1,
      name: 'GPT-4',
      provider: 'OpenAI',
      type: 'CHAT',
      contextLength: 8192,
      capabilities: ['chat', 'completion', 'embedding'],
      status: 'ACTIVE',
      callCount: 1250,
      successRate: 98.5,
      avgResponseTime: 1200
    },
    {
      id: 2,
      name: 'Claude-3',
      provider: 'Anthropic',
      type: 'CHAT',
      contextLength: 100000,
      capabilities: ['chat', 'completion'],
      status: 'ACTIVE',
      callCount: 890,
      successRate: 97.2,
      avgResponseTime: 1500
    },
    {
      id: 3,
      name: 'text-embedding-ada-002',
      provider: 'OpenAI',
      type: 'EMBEDDING',
      contextLength: 8192,
      capabilities: ['embedding'],
      status: 'ACTIVE',
      callCount: 2100,
      successRate: 99.1,
      avgResponseTime: 800
    }
  ]
}

const refreshModels = async () => {
  // Mock refresh
  ElMessage.info('正在刷新模型列表...')
  await new Promise(resolve => setTimeout(resolve, 1000))
  await loadModels()
  ElMessage.success('模型列表已刷新')
}

const testModel = async (model: Model) => {
  ElMessage.info(`正在测试模型 ${model.name}...`)
  // Mock test
  await new Promise(resolve => setTimeout(resolve, 2000))
  ElMessage.success(`模型 ${model.name} 测试成功`)
}

const configureModel = (model: Model) => {
  // TODO: 实现配置功能
  console.log('Configure model:', model)
}

const toggleModelStatus = async (model: Model) => {
  const action = model.status === 'ACTIVE' ? '禁用' : '启用'
  // Mock toggle
  model.status = model.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE'
  ElMessage.success(`模型${action}成功`)
}

const getModelTypeLabel = (type: string) => {
  switch (type) {
    case 'CHAT': return '对话模型'
    case 'COMPLETION': return '补全模型'
    case 'EMBEDDING': return '嵌入模型'
    default: return type
  }
}

const getCapabilityLabel = (cap: string) => {
  switch (cap) {
    case 'chat': return '对话'
    case 'completion': return '补全'
    case 'embedding': return '嵌入'
    default: return cap
  }
}

onMounted(() => {
  loadModels()
})
</script>

<style scoped>
.models {
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

.refresh-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: #67c23a;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.refresh-btn:hover {
  background: #5daf34;
}

.models-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.model-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s;
}

.model-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.model-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.model-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
}

.status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
  text-transform: uppercase;
}

.status.active {
  background: #e8f5e8;
  color: #52c41a;
}

.status.disabled {
  background: #fff2e8;
  color: #fa8c16;
}

.model-info {
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  margin-bottom: 8px;
  font-size: 14px;
}

.label {
  font-weight: 500;
  color: #666;
  min-width: 80px;
}

.value {
  color: #333;
}

.capabilities {
  display: flex;
  gap: 4px;
  flex-wrap: wrap;
}

.model-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 4px;
}

.stat {
  text-align: center;
}

.stat-label {
  display: block;
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.stat-value {
  display: block;
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.model-actions {
  display: flex;
  gap: 8px;
}

.test-btn,
.config-btn,
.status-btn {
  flex: 1;
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
}

.test-btn {
  background: #e6f7ff;
  color: #1890ff;
}

.test-btn:hover {
  background: #bae7ff;
}

.config-btn {
  background: #f6ffed;
  color: #52c41a;
}

.config-btn:hover {
  background: #d9f7be;
}

.status-btn.active {
  background: #fff2f0;
  color: #ff4d4f;
}

.status-btn.active:hover {
  background: #ffccc7;
}

.status-btn.disabled {
  background: #e8f5e8;
  color: #52c41a;
}

.status-btn.disabled:hover {
  background: #d9f7be;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-desc {
  margin: 8px 0 0 0;
  font-size: 14px;
}
</style>