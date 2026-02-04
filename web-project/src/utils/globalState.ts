import { reactive } from "vue";
import type { IGlobalConfig } from "../dto/IGlobalConfig";

// 从 localStorage 获取持久化数据
const getPersistedState = (): Partial<IGlobalConfig> => {
  try {
    const persistedState = localStorage.getItem('globalState');
    return persistedState ? JSON.parse(persistedState) : {};
  } catch (error) {
    console.error('Failed to parse persisted state:', error);
    return {};
  }
};

// 保存状态到 localStorage
const persistState = (state: IGlobalConfig) => {
  try {
    // 只保存需要持久化的字段
    const persistedData = {
      theme: state.theme,
      asideStatus: state.asideStatus,
      activeTab: state.activeTab,
      menuTabs: state.menuTabs,
      userInfo: state.userInfo,
    };
    localStorage.setItem('globalState', JSON.stringify(persistedData));
  } catch (error) {
    console.error('Failed to persist state:', error);
  }
};

// 从持久化存储获取初始状态
const persistedState = getPersistedState();

// 创建全局状态对象
const globalState = reactive<IGlobalConfig>({
  theme: persistedState.theme || "light",
  asideStatus: persistedState.asideStatus ?? true,
  menuList: [], // 菜单列表是动态加载的，不需要持久化
  userInfo: persistedState.userInfo || null,
  activeTab: persistedState.activeTab || "",
  menuTabs: persistedState.menuTabs || [],
});

// 使用 beforeunload 事件在页面卸载前保存状态
window.addEventListener('beforeunload', () => {
  persistState(globalState);
});

const updateActiveTab = (activeTab: string) => {
  globalState.activeTab = activeTab;
};

const addMenuTab = (args: any) => {
  const index = globalState.menuTabs.findIndex(
    (item: any) => item.resourcePath === args.resourcePath
  );
  if (index == -1) {
    globalState.menuTabs.push(args);
  } 
  console.log(index,args, globalState.menuTabs);

};

const removeMenuTab = (menuTab: any) => {
  globalState.menuTabs = globalState.menuTabs.filter(
    (item: any) => item.resourcePath !== menuTab.resourcePath
  );
};

// 更新菜单列表的方法
const updateMenuList = (menuList: any[]) => {
  globalState.menuList = menuList;
};

// 更新用户信息的方法
const updateUserInfo = (userInfo: any) => {
  globalState.userInfo = userInfo;
};

// 更新主题的方法
const updateTheme = (theme: string) => {
  globalState.theme = theme;
};

// 更新侧边栏状态的方法
const updateAsideStatus = (status: boolean) => {
  globalState.asideStatus = status;
};

export {
  globalState,
  addMenuTab,
  removeMenuTab,
  updateActiveTab,
  updateMenuList,
  updateUserInfo,
  updateTheme,
  updateAsideStatus,
};
