<template>
  <div class="tabs-container">
    <div class="tabs-wrapper">
      <div class="tab-item"
        v-for="tab in menuTabs"
        :key="tab.resourcePath"
        :class="{ 'active': activeTab === tab.resourcePath }"
        :data-resource-path="tab.resourcePath"
        @click="handleTabChange(tab.resourcePath)"
        @contextmenu.prevent="handleContextMenu($event, tab)"
      >
        <span class="tab-title">{{ tab.permName }}</span>
        <span 
          class="tab-close" 
          @click.stop="handleTabRemove(tab.resourcePath)"
        >
          ×
        </span>
      </div>
    </div>
    
    <!-- 右键菜单 -->
    <teleport to="body">
      <ul 
        v-if="showContextMenu" 
        class="context-menu" 
        :style="{ top: contextMenuY + 'px', left: contextMenuX + 'px' }"
        @mouseleave="hideContextMenu"
      >
        <li @click="refreshTab">刷新</li>
        <li @click="closeOtherTabs">关闭其他</li>
        <li @click="closeAllTabs">关闭全部</li>
      </ul>
    </teleport>
  </div>
</template>

<script lang="ts" setup>
import { computed, ref, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { globalState, updateActiveTab, removeMenuTab } from "../utils/globalState";

const router = useRouter();

const menuTabs = computed(() => {
  return globalState.menuTabs || [];
});

const activeTab = computed({
  get: () => globalState.activeTab,
  set: (value) => {
    updateActiveTab(value);
  },
});

// 右键菜单相关
const showContextMenu = ref(false);
const contextMenuX = ref(0);
const contextMenuY = ref(0);
const rightClickedTab = ref<any>(null);

const handleTabChange = (tabName: string) => {
  router.push(tabName);
};

const handleTabRemove = (targetName: string) => {
  const tabIndex = menuTabs.value.findIndex(tab => tab.resourcePath === targetName);
  if (tabIndex !== -1) {
    const tabToRemove = menuTabs.value[tabIndex];
    removeMenuTab(tabToRemove);
    
    // 如果关闭的是当前激活的标签页，则切换到相邻标签页
    if (targetName === activeTab.value) {
      let nextActiveTab = '';
      if (menuTabs.value.length > 1) {
        if (tabIndex === menuTabs.value.length) {
          // 如果关闭的是最后一个标签页，则激活前一个
          nextActiveTab = menuTabs.value[tabIndex - 1].resourcePath;
        } else {
          // 否则激活下一个
          nextActiveTab = menuTabs.value[tabIndex].resourcePath;
        }
      }
      updateActiveTab(nextActiveTab);
      if (nextActiveTab) {
        router.push(nextActiveTab);
      }
    }
  }
};

// 处理右键菜单
const handleContextMenu = (event: MouseEvent, tab: any) => {
  event.preventDefault();
  
  // 检查是否点击在标签上
  const target = event.target as HTMLElement;
  const tabPaneElement = target.closest('.el-tab-pane');
  
  if (!tabPaneElement) {
    const tabElement = target.closest('[data-resource-path]');
    if (tabElement) {
      const resourcePath = tabElement.getAttribute('data-resource-path');
      rightClickedTab.value = menuTabs.value.find((tab: any) => tab.resourcePath === resourcePath);
    }
  } else {
    // 如果点击在标签内容区域，尝试找到最近的标签页
    const tabParent = target.closest('[data-resource-path]');
    if (tabParent) {
      const resourcePath = tabParent.getAttribute('data-resource-path');
      rightClickedTab.value = menuTabs.value.find((tab: any) => tab.resourcePath === resourcePath);
    }
  }
  
  if (rightClickedTab.value) {
    showContextMenu.value = true;
    contextMenuX.value = event.clientX;
    contextMenuY.value = event.clientY;
  }
};

const hideContextMenu = () => {
  showContextMenu.value = false;
};

// 刷新当前标签页
const refreshTab = () => {
  if (rightClickedTab.value) {
    // 通过重新导航来实现刷新效果
    router.replace({ path: '/blank' }).then(() => {
      router.replace(rightClickedTab.value.resourcePath);
    });
  }
  hideContextMenu();
};

// 关闭其他标签页
const closeOtherTabs = () => {
  if (rightClickedTab.value) {
    globalState.menuTabs = [rightClickedTab.value];
    
    // 如果当前激活的标签页不在剩余的标签页中，则设置为右键点击的标签页
    if (globalState.activeTab !== rightClickedTab.value.resourcePath) {
      updateActiveTab(rightClickedTab.value.resourcePath);
      router.push(rightClickedTab.value.resourcePath);
    }
  }
  hideContextMenu();
};

// 关闭所有标签页
const closeAllTabs = () => {
  globalState.menuTabs = [];
  updateActiveTab('');
  router.push('/'); // 返回首页
  hideContextMenu();
};

// 监听全局鼠标点击事件来隐藏右键菜单
const handleClickOutside = (event: Event) => {
  const contextMenu = document.querySelector('.context-menu') as HTMLElement;
  if (showContextMenu.value && contextMenu && !contextMenu.contains(event.target as Node)) {
    hideContextMenu();
  }
};

// 添加事件监听器
if (typeof window !== 'undefined') {
  window.addEventListener('click', handleClickOutside);
}

// 组件卸载时移除事件监听器
onUnmounted(() => {
  if (typeof window !== 'undefined') {
    window.removeEventListener('click', handleClickOutside);
  }
});
</script>

<style scoped>
.tabs-container {
  position: relative;
  background: #fff;
  padding: 8px;
  border-bottom: 1px solid #e4e7ed;
  overflow: hidden;
}

.tabs-wrapper {
  display: flex;
  overflow-x: auto;
  gap: 4px;
  padding-bottom: 4px;
  /* 隐藏滚动条 */
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE/Edge */
}

.tabs-wrapper::-webkit-scrollbar {
  display: none; /* Chrome/Safari/Opera */
}

.tab-item {
  display: flex;
  align-items: center;
  padding: 0 8px;
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 4px 4px 0 0;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  user-select: none;
  transition: all 0.2s;
}

.tab-item:not(.active):hover {
  background: #e6e8eb;
}

.tab-item.active {
  background: #409eff;
  color: white;
  border-color: #409eff;
}

.tab-title {
  margin-right: 6px;
}

.tab-close {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  font-size: 12px;
  line-height: 18px;
  cursor: pointer;
  transition: all 0.2s;
}

.tab-item:not(.active) .tab-close:hover {
  background: #d8dce5;
  color: #909399;
}

.tab-item.active .tab-close:hover {
  background: #66b1ff;
  color: white;
}

.context-menu {
  position: fixed;
  background: white;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  list-style: none;
  padding: 4px 0;
  margin: 0;
  z-index: 9999;
  min-width: 120px;
}

.context-menu li {
  padding: 8px 16px;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
}

.context-menu li:hover {
  background-color: #f5f7fa;
  color: #409eff;
}
</style>
