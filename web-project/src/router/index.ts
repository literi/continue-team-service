import { createWebHashHistory, createRouter, RouterView } from "vue-router";
import MainLayout from '../layout/MainLayout.vue'
import Dashboard from '../views/Dashboard.vue'
import Tenants from '../views/Tenants.vue'

const routes = [
  {
    path: "/",
    name: "app",
    redirect: "/dashboard",
    component: MainLayout,
    meta: {
        requiresAuth: true
    },
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: Dashboard
      },
      {
        path: '/tenants',
        name: 'Tenants',
        component: Tenants
      },
      {
        path: '/tenants/create',
        name: 'CreateTenant',
        component: () => import('../views/CreateTenant.vue')
      },
      {
        path: '/projects',
        name: 'Projects',
        component: () => import('../views/Projects.vue')
      },
      {
        path: '/projects/create',
        name: 'CreateProject',
        component: () => import('../views/CreateProject.vue')
      },
      {
        path: '/users',
        name: 'Users',
        component: () => import('../views/Users.vue')
      },
      {
        path: '/users/create',
        name: 'CreateUser',
        component: () => import('../views/CreateUser.vue')
      },
      {
        path: '/models',
        name: 'Models',
        component: () => import('../views/Models.vue')
      },
      {
        path: '/configs',
        name: 'Configs',
        component: () => import('../views/Configs.vue')
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  }
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})


// 正确使用 beforeEach 做登录拦截（afterEach 无 next 参数，不适合拦截）
router.beforeEach((to, from, next) => {
  // 1. 获取 token
  const token = sessionStorage.getItem("token")
  
  // 2. 判断目标路由是否需要登录
  if (to.meta.requiresAuth) {
    // 3. 需要登录：有 token 则放行，无 token 则跳登录页
    if (token) {
      next() // 放行
    } else {
      next({ path: '/login' }) // 跳转到登录页
    }
  } else {
    // 不需要登录的路由（比如登录页），直接放行
    next()
  }
})
export { router }