import axios from "axios";
import type { AxiosInstance,AxiosRequestConfig, AxiosResponse } from "axios";

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
    const token = sessionStorage.getItem("token");
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
    if (error.response?.status === 401) {
      sessionStorage.removeItem("token");
      return request(error.config); // 重新发送原始请求
    }
    throw error;
  },
);


export { request };

