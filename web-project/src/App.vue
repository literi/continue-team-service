<template>
  <el-config-provider>
    <router-view />
  </el-config-provider>
</template>
<script setup lang="ts">
import { provide } from "vue";
import { globalState } from "./utils/globalState";

// 使用共享的全局状态而不是本地状态
provide("globalConfig", globalState);
</script>