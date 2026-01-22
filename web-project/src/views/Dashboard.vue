<template>
  <div class="dashboard">
    <div class="page-header">
      <h2>仪表板</h2>
      <p>Continue 扩展服务管理概览</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon">
            <el-icon class="stat-icon-large"><OfficeBuilding /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalTenants }}</div>
            <div class="stat-label">总租户数</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon">
            <el-icon class="stat-icon-large"><FolderOpened /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalProjects }}</div>
            <div class="stat-label">总项目数</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon">
            <el-icon class="stat-icon-large"><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ stats.totalUsers }}</div>
            <div class="stat-label">总用户数</div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon">
            <el-icon class="stat-icon-large"><Monitor /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-number">{{ formatNumber(stats.modelUsage) }}</div>
            <div class="stat-label">模型使用量</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <div class="charts-grid">
      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>模型使用统计</span>
            <el-select v-model="timeRange" size="small" style="width: 120px">
              <el-option label="最近7天" value="7d" />
              <el-option label="最近30天" value="30d" />
              <el-option label="最近90天" value="90d" />
            </el-select>
          </div>
        </template>
        <div class="chart-container">
          <div id="modelUsageChart" style="width: 100%; height: 300px;"></div>
        </div>
      </el-card>

      <el-card class="chart-card">
        <template #header>
          <div class="card-header">
            <span>租户活跃度</span>
          </div>
        </template>
        <div class="chart-container">
          <div id="tenantActivityChart" style="width: 100%; height: 300px;"></div>
        </div>
      </el-card>
    </div>

    <!-- 最近活动 -->
    <el-card class="activity-card">
      <template #header>
        <span>最近活动</span>
      </template>
      <el-timeline>
        <el-timeline-item
          v-for="activity in recentActivities"
          :key="activity.id"
          :timestamp="activity.timestamp"
        >
          <div class="activity-content">
            <el-tag :type="activity.type">{{ activity.action }}</el-tag>
            <span>{{ activity.description }}</span>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'
import {
  OfficeBuilding,
  FolderOpened,
  User,
  Monitor
} from '@element-plus/icons-vue'

interface Stats {
  totalTenants: number
  totalProjects: number
  totalUsers: number
  activeUsers: number
  modelUsage: number
}

interface Activity {
  id: number
  action: string
  description: string
  type: string
  timestamp: string
}

const stats = ref<Stats>({
  totalTenants: 0,
  totalProjects: 0,
  totalUsers: 0,
  activeUsers: 0,
  modelUsage: 0
})

const timeRange = ref('7d')
const recentActivities = ref<Activity[]>([
  {
    id: 1,
    action: '创建项目',
    description: '用户张三创建了新项目 "AI助手开发"',
    type: 'success',
    timestamp: '2024-01-15 14:30:00'
  },
  {
    id: 2,
    action: '添加用户',
    description: '管理员添加了新用户李四到租户A',
    type: 'info',
    timestamp: '2024-01-15 13:45:00'
  },
  {
    id: 3,
    action: '更新配置',
    description: '租户B更新了模型配置参数',
    type: 'warning',
    timestamp: '2024-01-15 12:20:00'
  },
  {
    id: 4,
    action: '删除项目',
    description: '项目 "测试项目" 已被删除',
    type: 'danger',
    timestamp: '2024-01-15 11:10:00'
  }
])

const formatNumber = (num: number): string => {
  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K'
  }
  return num.toString()
}

const loadStats = async () => {
 
}

const initCharts = () => {
  // 模型使用统计图表
  const modelUsageChart = echarts.init(document.getElementById('modelUsageChart')!)
  const modelUsageOption = {
    title: {
      text: '模型使用趋势'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['GPT-4', 'GPT-3.5', 'Claude', '本地模型']
    },
    xAxis: {
      type: 'category',
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: 'GPT-4',
        type: 'line',
        data: [120, 132, 101, 134, 90, 230, 210]
      },
      {
        name: 'GPT-3.5',
        type: 'line',
        data: [220, 182, 191, 234, 290, 330, 310]
      },
      {
        name: 'Claude',
        type: 'line',
        data: [150, 232, 201, 154, 190, 330, 410]
      },
      {
        name: '本地模型',
        type: 'line',
        data: [320, 302, 301, 334, 390, 330, 320]
      }
    ]
  }
  modelUsageChart.setOption(modelUsageOption)

  // 租户活跃度图表
  const tenantActivityChart = echarts.init(document.getElementById('tenantActivityChart')!)
  const tenantActivityOption = {
    title: {
      text: '租户活跃度分布'
    },
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '活跃度',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 35, name: '高活跃' },
          { value: 25, name: '中活跃' },
          { value: 20, name: '低活跃' },
          { value: 20, name: '不活跃' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  tenantActivityChart.setOption(tenantActivityOption)
}

onMounted(() => {
  loadStats()
  initCharts()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.page-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 24px;
  font-weight: 600;
}

.page-header p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  flex-shrink: 0;
}

.stat-icon-large {
  font-size: 32px;
}

.stat-icon-large:nth-child(1) {
  color: #409eff;
}

.stat-icon-large:nth-child(2) {
  color: #67c23a;
}

.stat-icon-large:nth-child(3) {
  color: #e6a23c;
}

.stat-icon-large:nth-child(4) {
  color: #f56c6c;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  min-height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 300px;
}

.activity-card {
  margin-top: 24px;
}

.activity-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.activity-content span {
  color: #606266;
}
</style>