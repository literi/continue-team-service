import { createApp } from "vue";
import ElementPlus from "element-plus";
import zhCn from "element-plus/es/locale/lang/zh-cn";
import { router, initRouter } from "./router";
import "./style.css";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";
// import 'dayjs/locale/zh-cn'
import "element-plus/theme-chalk/index.css";
import App from "./App.vue";

async function initApp() {
  // init router
  await initRouter(router);
  const app = createApp(App);
  for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
  }
  app.use(ElementPlus, {
    locale: zhCn,
  });
  app.use(router);
  app.mount("#app");
}
initApp()