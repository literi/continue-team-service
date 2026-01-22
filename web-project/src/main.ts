import { createApp, h, provide } from "vue";
import { RouterView } from "vue-router";
import ElementPlus from "element-plus";
import { ElConfigProvider } from "element-plus";
import zhCn from "element-plus/es/locale/lang/zh-cn";
import { router } from "./router";
import axios from "axios";
import "./style.css";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
// import 'dayjs/locale/zh-cn'
import "element-plus/theme-chalk/index.css";
import { reactive } from "vue";
import type { IGlobalConfig, IUserInfo } from "./dto/IGlobalConfig";

// 配置axios
axios.defaults.baseURL = "/";
axios.defaults.timeout = 10000;

// 请求拦截器
axios.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);

// 响应拦截器
axios.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    if (error.response?.status === 401) {
      // 未授权，跳转到登录页
      localStorage.removeItem("token");
      localStorage.removeItem("currentUser");
      router.push("/login");
    }
    return Promise.reject(error);
  },
);

const app = createApp({
  setup() {
    provide(
      "globalConfig",
      reactive<IGlobalConfig>({
        theme: "light",
        asideStatus: true,
        menuList: [],
        userInfo: null,
      }),
    );
  },
  render() {
    return h(ElConfigProvider, {}, [h(RouterView)]);
  },
});

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

app.use(ElementPlus, {
  locale: zhCn,
});

app.use(router);
app.mount("#app");
