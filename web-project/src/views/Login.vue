<template>
  <div class="login-container">
    <div class="login-form">
      <div class="logo-section">
        <h1>Continue Admin</h1>
        <p>Continue 扩展服务管理系统</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        label-position="top"
        class="login-form-content"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="验证码" prop="captcha">
          <el-input
            v-model="loginForm.captcha"
            placeholder="请输入验证码"
            size="large"
          >
            <template #prefix>
              <img :src="imgBase64" alt="Captcha" class="captcha-img" @click="refreshCaptcha" />
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-button"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="demo-account">
        <p>演示账号：</p>
        <p>用户名：admin | 密码：123456</p>
      </div>
    </div>

    <div class="login-bg">
      <div class="bg-content">
        <h2>欢迎使用 Continue 管理系统</h2>
        <p>高效管理您的 AI 扩展服务</p>
        <div class="features">
          <div class="feature-item">
            <el-icon size="24"><OfficeBuilding /></el-icon>
            <span>多租户管理</span>
          </div>
          <div class="feature-item">
            <el-icon size="24"><User /></el-icon>
            <span>用户权限控制</span>
          </div>
          <div class="feature-item">
            <el-icon size="24"><Monitor /></el-icon>
            <span>模型监控统计</span>
          </div>
          <div class="feature-item">
            <el-icon size="24"><Setting /></el-icon>
            <span>灵活配置管理</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { getCaptcha, userLogin } from "../api/auth";
import {
  User,
  Lock,
  OfficeBuilding,
  Monitor,
  Setting,
} from "@element-plus/icons-vue";

const router = useRouter();
const loginFormRef = ref();
const loading = ref(false);
const imgBase64 = ref("");
const loginForm = reactive({
  username: "admin",
  password: "123456",
  captcha: "",
  uuid: "",
});

onMounted(() => {
  refreshCaptcha();
});

const refreshCaptcha = () => {
  getCaptcha().then((res) => {
    imgBase64.value = res.data.img;
    loginForm.uuid = res.data.uuid;
  });
};

const loginRules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码长度不能少于6位", trigger: "blur" },
  ],
};

const handleLogin = async () => {
  if (!loginFormRef.value) return;

  await loginFormRef.value.validate(async (valid: boolean) => {
    if (valid) {
      loading.value = true;
      try {
        // sessionStorage.setItem('token', '123456')
        userLogin({
          username: loginForm.username,
          password: loginForm.password,
          captcha: loginForm.captcha,
          uuid: loginForm.uuid,
        }).then((res) => {
          sessionStorage.setItem("token", `${res.data.type} ${res.data.token}`);
          router.push('/dashboard')
        });
      } catch (error: any) {
        console.error("Login failed:", error);
        const message =
          error.response?.data?.message || "登录失败，请检查用户名和密码";
        ElMessage.error(message);
      } finally {
        loading.value = false;
      }
    }
  });
};
</script>

<style scoped>
.login-container {
  display: flex;
  height: 100vh;
  background-color: #f5f5f5;
  flex-direction: row-reverse;
}

.login-form {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
  background-color: #fff;
  max-width: 400px;
}

.logo-section {
  text-align: center;
  margin-bottom: 40px;
}

.logo-section h1 {
  margin: 0 0 8px 0;
  color: #409eff;
  font-size: 28px;
  font-weight: 600;
}

.logo-section p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.login-form-content {
  width: 100%;
  max-width: 320px;
}

.login-button {
  width: 100%;
}

.demo-account {
  margin-top: 24px;
  text-align: center;
  color: #909399;
  font-size: 12px;
}

.demo-account p {
  margin: 4px 0;
}

.login-bg {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.bg-content {
  color: #fff;
  text-align: center;
  max-width: 400px;
}

.bg-content h2 {
  margin: 0 0 16px 0;
  font-size: 32px;
  font-weight: 600;
}

.bg-content p {
  margin: 0 0 32px 0;
  font-size: 16px;
  opacity: 0.9;
}

.features {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  backdrop-filter: blur(10px);
}

.feature-item span {
  font-size: 14px;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }

  .login-form {
    flex: none;
    min-height: 50vh;
    max-width: none;
  }

  .login-bg {
    flex: none;
    min-height: 50vh;
  }

  .features {
    grid-template-columns: 1fr;
  }
}
</style>
