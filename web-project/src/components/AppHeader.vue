<template>
  <div class="header-container">
    <div class="header-left">
      <div class="logo">
        <img src="/src/assets/logo.png" alt="Logo" class="logo-img" />
        <span class="logo-text">Continue Admin</span>
      </div>
    </div>
    
    <div class="header-right">
      <el-dropdown class="user-dropdown" @command="handleCommand">
        <div class="user-info">
          <el-avatar :size="32" class="user-avatar">
            {{ userInfo.initials }}
          </el-avatar>
          <span class="user-name">{{ userInfo.name }}</span>
          <el-icon class="el-icon--right">
            <arrow-down />
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>
              个人资料
            </el-dropdown-item>
            <el-dropdown-item command="password">
              <el-icon><Lock /></el-icon>
              修改密码
            </el-dropdown-item>
            <el-dropdown-item command="settings">
              <el-icon><Setting /></el-icon>
              系统设置
            </el-dropdown-item>
            <el-dropdown-item divided command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  ArrowDown, 
  User, 
  Lock, 
  Setting, 
  SwitchButton 
} from '@element-plus/icons-vue'

const router = useRouter()

// 模拟用户信息
const userInfo = reactive({
  name: 'Admin',
  initials: 'A',
  avatar: ''
})

// 处理下拉菜单命令
const handleCommand = async (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'password':
      router.push('/change-password')
      break
    case 'settings':
      router.push('/settings')
      break
    case 'logout':
      try {
        await ElMessageBox.confirm(
          '确定要退出登录吗？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )
        // 清除用户信息
        sessionStorage.removeItem('token')
        localStorage.removeItem('token')
        localStorage.removeItem('currentUser')
        
        // 跳转到登录页
        router.push('/login')
        ElMessage.success('已退出登录')
      } catch {
        // 用户取消操作
      }
      break
    default:
      break
  }
}

// 获取用户信息的方法（可以从API获取真实用户信息）
const fetchUserInfo = async () => {
  // TODO: 从API获取用户信息
  // const response = await getUserInfo()
  // userInfo.name = response.data.name
  // userInfo.initials = response.data.name.charAt(0).toUpperCase()
}

// 组件挂载时获取用户信息
fetchUserInfo()
</script>

<style lang="scss" scoped>
.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: $app-header-height;
  padding: 0 20px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  box-sizing: border-box;
  border-bottom: 1px solid #e4e7ed;

  .header-left {
    display: flex;
    align-items: center;
    
    .logo {
      display: flex;
      align-items: center;
      
      .logo-img {
        height: 32px;
        margin-right: 12px;
      }
      
      .logo-text {
        font-size: 18px;
        font-weight: 600;
        color: #1890ff;
      }
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    
    .user-dropdown {
      cursor: pointer;
      display: flex;
      align-items: center;
      
      .user-info {
        display: flex;
        align-items: center;
        padding: 4px;
        border-radius: 4px;
        transition: background-color 0.2s;
        
        &:hover {
          background-color: #f5f5f5;
        }
        
        .user-avatar {
          margin-right: 8px;
          background-color: #1890ff;
          color: #fff;
        }
        
        .user-name {
          margin-right: 8px;
          font-size: 14px;
          color: #333;
        }
        
        .el-icon {
          font-size: 12px;
          color: #999;
        }
      }
    }
  }
}
</style>
