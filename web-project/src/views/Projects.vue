<template>
  <div class="projects">
    <div class="page-header">
      <h2>项目管理</h2>
      <router-link to="/projects/create" class="create-btn">
        <i class="icon-plus"></i>
        创建项目
      </router-link>
    </div>

    <div class="projects-grid">
      <div v-for="project in projects" :key="project.id" class="project-card">
        <div class="project-header">
          <h3>{{ project.name }}</h3>
          <span :class="['status', project.status.toLowerCase()]">{{ project.status }}</span>
        </div>
        <p class="project-description">{{ project.description }}</p>
        <div class="project-meta">
          <span>租户: {{ project.tenantName }}</span>
          <span>创建时间: {{ formatDate(project.createdAt) }}</span>
        </div>
        <div class="project-actions">
          <button @click="editProject(project)" class="edit-btn">编辑</button>
          <button @click="deleteProject(project)" class="delete-btn">删除</button>
        </div>
      </div>
    </div>

    <div v-if="projects.length === 0" class="empty-state">
      <p>暂无项目数据</p>
      <router-link to="/projects/create" class="create-link">创建第一个项目</router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

interface Project {
  id: number
  name: string
  description: string
  tenantName: string
  status: string
  createdAt: string
}

const projects = ref<Project[]>([])

const loadProjects = async () => {
  // Mock data
  projects.value = [
    {
      id: 1,
      name: 'AI 代码助手项目',
      description: '基于 Continue 的团队代码辅助工具',
      tenantName: '研发部',
      status: 'ACTIVE',
      createdAt: '2026-01-20T10:00:00Z'
    },
    {
      id: 2,
      name: '数据分析平台',
      description: '企业级数据分析和可视化平台',
      tenantName: '数据部',
      status: 'ACTIVE',
      createdAt: '2026-01-18T14:30:00Z'
    }
  ]
}

const editProject = (project: Project) => {
  // TODO: 实现编辑功能
  console.log('Edit project:', project)
}

const deleteProject = async (project: Project) => {
  if (confirm(`确定要删除项目 "${project.name}" 吗？`)) {
    // Mock delete
    projects.value = projects.value.filter(p => p.id !== project.id)
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadProjects()
})
</script>

<style scoped>
.projects {
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

.projects-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.project-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s;
}

.project-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.project-header h3 {
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

.status.archived {
  background: #fff2e8;
  color: #fa8c16;
}

.project-description {
  color: #666;
  margin-bottom: 16px;
  line-height: 1.5;
}

.project-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
  margin-bottom: 16px;
}

.project-actions {
  display: flex;
  gap: 8px;
}

.edit-btn,
.delete-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
}

.edit-btn {
  background: #e6f7ff;
  color: #1890ff;
}

.edit-btn:hover {
  background: #bae7ff;
}

.delete-btn {
  background: #fff2f0;
  color: #ff4d4f;
}

.delete-btn:hover {
  background: #ffccc7;
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