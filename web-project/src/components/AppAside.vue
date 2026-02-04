<template>
  <div class="menu-container">
    <div class="menu-list">
      <el-menu
        class="app-menu-vertical"
        :collapse="isCollapse"
        router
      >
        <el-menu-item
          v-for="item in menuList"
          :key="item.resourcePath"
          :index="item.resourcePath"
          @click="handleMenuItemClick(item)"
        >
          <el-icon><item.icon /></el-icon>
          <template #title>{{ item.permName }}</template>
        </el-menu-item>
        <!-- <el-sub-menu index="1">
          <template #title>
            <el-icon><location /></el-icon>
            <span>Navigator One</span>
          </template>
          <el-menu-item-group>
            <template #title><span>Group One</span></template>
            <el-menu-item index="1-1">item one</el-menu-item>
            <el-menu-item index="1-2">item two</el-menu-item>
          </el-menu-item-group>
          <el-menu-item-group title="Group Two">
            <el-menu-item index="1-3">item three</el-menu-item>
          </el-menu-item-group>
          <el-sub-menu index="1-4">
            <template #title><span>item four</span></template>
            <el-menu-item index="1-4-1">item one</el-menu-item>
          </el-sub-menu>
        </el-sub-menu> -->
      </el-menu>
    </div>
    <el-button
      class="menu-btn"
      type="primary"
      @click="handleClick"
      :icon="Operation"
    />
  </div>
</template>
<script setup lang="ts">
import {
  Operation,
} from "@element-plus/icons-vue";
import { computed, inject } from "vue";
import { addMenuTab } from "../utils/globalState";
import type { IGlobalConfig } from "../dto/IGlobalConfig";
const globalConfig = inject<IGlobalConfig>("globalConfig");

const handleClick = () => {
  if (globalConfig) {
    globalConfig.asideStatus = !globalConfig?.asideStatus;
  }
};

const isCollapse = computed(() => {
  return !globalConfig?.asideStatus;
});

const menuList = computed(() => {
  return globalConfig?.menuList;
});

const handleMenuItemClick = (item: any) => {
  addMenuTab(item);
};
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
    // padding: $app-padding;
  }
  .menu-btn {
    width: calc(100% - $app-padding * 2);
    padding: $app-padding;
    margin-top: 4px;
  }
}
</style>
