<template>
  <el-container class="app-container">
    <el-header class="app-header">
        <AppHeader />
    </el-header>
    <el-container class="app-content">
      <el-aside :class="globalConfig!.asideStatus ? 'is-open' : 'is-close'">
        <AppAside />
      </el-aside>
      <div style="width: 100%; border-left: 2px solid #e3e3e3">
        <app-tabs class="app-tabs" />
        <el-main class="app-main">
          <router-view></router-view>
        </el-main>
      </div>
    </el-container>
  </el-container>
</template>

<script lang="ts" setup>
import { inject, onMounted } from "vue";
import AppTabs from "../components/AppTabs.vue";
import AppAside from "../components/AppAside.vue";
import AppHeader from "../components/AppHeader.vue";
import type { IGlobalConfig } from "../dto/IGlobalConfig";
import { getMenuList, getUserInfo } from "../api/profile";
const globalConfig = inject<IGlobalConfig>("globalConfig");
onMounted(() => {
  // 初始化全局配置
  // 获取用户信息
  getUserInfo().then(res=>{
    console.log(res)
  })
  getMenuList().then(res=>{
    console.log(res)
  })
  // 请求菜单数据

  console.log(globalConfig);
});
</script>
<style lang="scss" scoped>
.app-container {
  height: 100%;
  .app-header {
    height: $app-header-height;
    background: #e3e3e3;
    padding: 0;
    border-bottom: 1px solid #e3e3e3;
  }
  .app-content {
    flex: 1 0 auto;
    height: calc(100% - $app-header-height);

    .is-open {
      width: $app-aside-width;
    }
    .is-close {
      width: $app-aside-close-width;
    }
    .app-tabs {
      height: $app-tabs-height;
      background: #fff;
      padding: $app-padding;
      border-bottom: 1px solid rgb(214, 214, 214);
    }
    .app-main {
      height: calc(100% - $app-tabs-height);
      padding: $app-padding;
      overflow: auto;
      background: #fff;
    }
  }
}
</style>
