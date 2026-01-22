<template>
  <div class="menu-container">
    <div class="menu-list">
      <div style="height: 1200px"></div>
    </div>
    <el-button class="menu-btn" type="primary" @click="handleClick" :icon="Operation" />
  </div>
</template>
<script setup lang="ts">
import {
  ArrowLeft,
  ArrowRight,
  House,
  Notification,
  Operation,
} from "@element-plus/icons-vue";
import { inject } from "vue";
const globalConfig = inject<IGlobalConfig>("globalConfig")

const handleClick = () => {
  if(globalConfig){
   globalConfig.asideStatus = !globalConfig?.asideStatus
  }
}
</script>
<style lang="scss" scoped>
.menu-container {
  background: #fff;
  width: 100%;
  height: 100%;
  overflow: auto;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  .menu-list {
    width: 100%;
    height: calc(100% - 40px);
    overflow: auto;
    padding: $app-padding;
  }
  .menu-btn {
    width: calc(100% - $app-padding * 2);
    padding: $app-padding;
    margin-top: 4px;
    
  }
}
</style>
