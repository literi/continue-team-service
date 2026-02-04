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
            传统登录
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button
            type="default"
            size="large"
            class="login-button"
            @click="handleOAuthLogin"
          >
            OAuth 2.1 + PKCE 登录
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

// OAuth2.1 + PKCE 相关变量
const codeVerifier = ref("");
const codeChallenge = ref("");

onMounted(() => {
  refreshCaptcha();
});

// 生成随机字符串作为 code_verifier
const generateRandomString = (length: number) => {
  const array = new Uint8Array(length);
  crypto.getRandomValues(array);
  return Array.from(array, byte => byte.toString(36)).join('').substring(0, length);
};

// 生成 SHA256 哈希的 base64url 编码
const generateCodeChallenge = async (verifier: string) => {
  const encoder = new TextEncoder();
  const data = encoder.encode(verifier);
  const hashed = await crypto.subtle.digest('SHA-256', data);
  
  // Base64 URL 编码
  return btoa(String.fromCharCode(...new Uint8Array(hashed)))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=/g, '');
};

// 初始化 PKCE 参数
const initPKCE = async () => {
  codeVerifier.value = generateRandomString(64);
  codeChallenge.value = await generateCodeChallenge(codeVerifier.value);
};

// OAuth2.1 PKCE 登录
const handleOAuthLogin = async () => {
  await initPKCE();
  
  const clientId = "my-client-id"; // 从配置中获取
  const redirectUri = encodeURIComponent(window.location.origin + "/callback");
  const state = generateRandomString(16);
  const scope = "read write";
  
  // 保存 state 到 sessionStorage 用于后续验证
  sessionStorage.setItem("oauth_state", state);
  sessionStorage.setItem("code_verifier", codeVerifier.value);
  
  const authUrl = `/oauth2/authorize?response_type=code&client_id=${clientId}&redirect_uri=${redirectUri}&state=${state}&scope=${scope}&code_challenge=${codeChallenge.value}&code_challenge_method=S256`;
  
  window.location.href = authUrl;
};

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
        // 传统登录方式（保留向后兼容）
        userLogin({
          username: loginForm.username,
          password: loginForm.password,
          captcha: loginForm.captcha,
          uuid: loginForm.uuid,
        }).then((res) => {
          // 检查响应数据是否存在
          if (!res || !res.data) {
            ElMessage.error('登录响应数据异常');
            return;
          }
          
          // 存储访问令牌和刷新令牌
          if (res.data.accessToken) {
            const accessToken = `${res.data.tokenType || 'Bearer'} ${res.data.accessToken}`;
            const refreshToken = res.data.refreshToken;
            
            sessionStorage.setItem("access_token", accessToken);
            if (refreshToken) {
              sessionStorage.setItem("refresh_token", refreshToken);
            }
            
            // 设置访问令牌过期时间（15分钟）
            const now = Date.now();
            sessionStorage.setItem("access_token_expires_at", (now + 15 * 60 * 1000).toString());
            
            // 如果有刷新令牌，设置其过期时间（24小时）
            if (refreshToken) {
              sessionStorage.setItem("refresh_token_expires_at", (now + 24 * 60 * 60 * 1000).toString());
            }
          } else {
            // 兼容旧的 token 格式
            if (res.data.type && res.data.token) {
              sessionStorage.setItem("token", `${res.data.type} ${res.data.token}`);
            } else {
              // 如果既没有新的也没有旧的token格式，提示错误
              ElMessage.error('登录响应缺少令牌信息');
              return;
            }
          }
          
          router.push('/dashboard');
        }).catch((err) => {
          console.error('Login error:', err);
          const message = err.response?.data?.message || '登录失败，请检查用户名和密码';
          ElMessage.error(message);
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
