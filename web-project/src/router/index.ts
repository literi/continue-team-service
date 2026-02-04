import { createWebHashHistory, createRouter } from "vue-router";
import MainLayout from "../layout/MainLayout.vue";
import { getMenuList } from "../api/profile";
import { updateMenuList } from "../utils/globalState";
const modulesRoute = import.meta.glob("../views/**/*.vue");

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
  {
    path: "/login",
    name: "Login",
    component: () => import("../views/Login.vue"),
  },
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    component: () => import("../views/NotFound.vue"),
  },
],
});

// 正确使用 beforeEach 做登录拦截（afterEach 无 next 参数，不适合拦截）
router.beforeEach((to, from, next) => {
  // 1. 获取 token
  const token = sessionStorage.getItem("token");

  // 2. 判断目标路由是否需要登录
  if (to.meta.requiresAuth) {
    // 3. 需要登录：有 token 则放行，无 token 则跳登录页
    if (token) {
      next(); // 放行
    } else {
      next({ path: "/login" }); // 跳转到登录页
    }
  } else {
    // 不需要登录的路由（比如登录页），直接放行
    next();
  }
});

async function initRouter(router: any) {
  router.removeRoute("AppLayout");
   await getMenuList().then((res) => {
    // 将API返回的完整菜单数据更新到全局状态
    updateMenuList(res.data);
    router.addRoute({
      path: "/",
      name: "AppLayout",
      component: MainLayout,
      children: [],
    });
    const listMenu = res.data.map((el) => {
      return {
        name: el.permName,
        path: el.resourcePath,
        component: modulesRoute[`../views${el.resourcePath}.vue`]!,
      };
    });
    listMenu.forEach((el) => {
      router.addRoute("AppLayout", el);
    });
  }).catch((err) => {
    console.log(err);
  });
  return Promise.resolve(true);
}

export { router, initRouter };
