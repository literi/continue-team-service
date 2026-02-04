import axios from "axios";
import type { AxiosInstance,AxiosRequestConfig, AxiosResponse } from "axios";
import { router } from "../router";

// 定义响应数据的通用格式
interface ApiResponse<T = any> {
  data: T;
  status: number;
  statusText: string;
}

const request = axios.create({
  baseURL: "",
  timeout: 1000,
});

request.interceptors.request.use(
  function (config: AxiosRequestConfig) {
    // 优先使用新的 access_token，如果不存在则使用旧的 token
    let token = sessionStorage.getItem("access_token");
    if (!token) {
      token = sessionStorage.getItem("token");
    }
    if (token) {
      config.headers = {
        ...config.headers,
        ["Authorization"]: token,
      };
    }
    return config;
  },
  function (error) {
    // 对请求错误做些什么
    return Promise.reject(error);
  },
);

request.interceptors.response.use(
  function (response) {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      // 尝试使用刷新令牌获取新的访问令牌
      const refreshToken = sessionStorage.getItem("refresh_token");
      
      if (refreshToken) {
        try {
          // 检查刷新令牌是否过期
          const refreshTokenExpiresAt = sessionStorage.getItem("refresh_token_expires_at");
          if (refreshTokenExpiresAt && Date.now() > parseInt(refreshTokenExpiresAt)) {
            // 刷新令牌已过期，清除所有token并跳转到登录页
            sessionStorage.removeItem("access_token");
            sessionStorage.removeItem("refresh_token");
            sessionStorage.removeItem("access_token_expires_at");
            sessionStorage.removeItem("refresh_token_expires_at");
            router.push("/login");
            throw error;
          }
          
          // 使用刷新令牌获取新的访问令牌
          const refreshResponse = await axios.post('/oauth2/token', {
            grant_type: 'refresh_token',
            refresh_token: refreshToken,
            client_id: 'my-client-id' // 从配置中获取
          }, {
            headers: {
              'Content-Type': 'application/x-www-form-urlencoded',
            }
          });
          
          if (refreshResponse.data.access_token) {
            // 更新访问令牌
            const newAccessToken = `${refreshResponse.data.token_type || 'Bearer'} ${refreshResponse.data.access_token}`;
            sessionStorage.setItem("access_token", newAccessToken);
            
            // 更新访问令牌过期时间（15分钟）
            const now = Date.now();
            sessionStorage.setItem("access_token_expires_at", (now + 15 * 60 * 1000).toString());
            
            // 如果返回了新的刷新令牌，也更新它
            if (refreshResponse.data.refresh_token) {
              sessionStorage.setItem("refresh_token", refreshResponse.data.refresh_token);
              // 更新刷新令牌过期时间（24小时）
              sessionStorage.setItem("refresh_token_expires_at", (now + 24 * 60 * 60 * 1000).toString());
            }
            
            // 更新请求头并重试原始请求
            originalRequest.headers['Authorization'] = newAccessToken;
            return request(originalRequest);
          }
        } catch (refreshError) {
          console.error('Token refresh failed:', refreshError);
          // 刷新令牌失败，清除所有token并跳转到登录页
          sessionStorage.removeItem("access_token");
          sessionStorage.removeItem("refresh_token");
          sessionStorage.removeItem("access_token_expires_at");
          sessionStorage.removeItem("refresh_token_expires_at");
          router.push("/login");
          throw error;
        }
      } else {
        // 没有刷新令牌，直接跳转到登录页
        sessionStorage.removeItem("access_token");
        sessionStorage.removeItem("access_token_expires_at");
        router.push("/login");
        throw error;
      }
    }
    throw error;
  },
);


export { request };

