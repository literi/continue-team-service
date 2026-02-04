<template>
  <div class="app-table">
    <!-- 搜索区域 -->
    <div class="table-search" v-if="showSearch">
      <app-form
        :fields="normalizedSearchFields"
        v-model="searchForm"
        :show-actions="true"
        :show-submit="false"
        :show-reset="false"
        @field-change="handleSearchFieldChange"
      >
        <template #actions>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </template>
      </app-form>
    </div>

    <!-- 工具栏 -->
    <div class="table-toolbar">
      <div class="toolbar-left">
        <slot name="toolbar-left"></slot>
      </div>
      <div class="toolbar-right">
        <slot name="toolbar-right">
          <el-button @click="refresh" :icon="Refresh" plain>刷新</el-button>
        </slot>
      </div>
    </div>

    <!-- 表格主体 -->
    <el-table
      v-loading="loading"
      :data="tableData"
      :columns="columns"
      :border="border"
      :stripe="stripe"
      :row-key="rowKey"
      @selection-change="handleSelectionChange"
      @sort-change="handleSortChange"
      v-bind="$attrs"
    >
      <!-- 默认列渲染 -->
      <el-table-column
        v-for="column in columns"
        :key="column.prop"
        v-bind="column"
        :formatter="column.formatter"
      >
        <template #default="scope">
          <!-- 自定义插槽内容 -->
          <slot
            :name="column.slotName || column.prop"
            :row="scope.row"
            :index="scope.$index"
            :column="column"
          >
            <!-- 默认渲染 -->
            <template
              v-if="column.formatter && typeof column.formatter === 'function'"
            >
              {{
                column.formatter(
                  scope.row,
                  scope.column,
                  scope.row[column.prop],
                  scope.$index
                )
              }}
            </template>
            <template v-else-if="column.type === 'status'">
              <el-tag :type="getStatusTagType(scope.row[column.prop])">
                {{ getStatusText(scope.row[column.prop]) }}
              </el-tag>
            </template>
            <template v-else>
              {{ scope.row[column.prop] }}
            </template>
          </slot>
        </template>
      </el-table-column>

      <!-- 操作列 -->
      <el-table-column
        v-if="operationColumn"
        label="操作"
        :fixed="operationFixed"
        :width="operationWidth"
      >
        <template #default="scope">
          <slot name="operation" :row="scope.row" :index="scope.$index"></slot>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="table-pagination" v-if="showPagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="pageSizes"
        :layout="paginationLayout"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch, PropType } from "vue";
import { Refresh } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import AppForm from "./AppForm.vue";

import type { TableColumn, SearchField } from "../dto/ITable";

// 定义响应式数据
const props = defineProps({
  // 数据获取函数
  fetchData: {
    type: Function as PropType<(params: any) => Promise<any>>,
    required: true,
  },
  // 表格列配置
  columns: {
    type: Array as PropType<TableColumn[]>,
    default: () => [],
  },
  // 是否显示边框
  border: {
    type: Boolean,
    default: true,
  },
  // 是否显示斑马纹
  stripe: {
    type: Boolean,
    default: true,
  },
  // 表格主键字段
  rowKey: {
    type: String,
    default: "id",
  },
  // 是否显示搜索区域
  showSearch: {
    type: Boolean,
    default: true,
  },
  // 搜索字段配置
  searchFields: {
    type: Array as PropType<SearchField[]>,
    default: () => [],
  },
  // 是否显示分页
  showPagination: {
    type: Boolean,
    default: true,
  },
  // 分页布局
  paginationLayout: {
    type: String,
    default: "total, sizes, prev, pager, next, jumper",
  },
  // 页面大小选项
  pageSizes: {
    type: Array as PropType<number[]>,
    default: () => [10, 20, 50, 100],
  },
  // 操作列相关配置
  operationColumn: {
    type: Boolean,
    default: true,
  },
  operationFixed: {
    type: [Boolean, String],
    default: "right",
  },
  operationWidth: {
    type: Number,
    default: 150,
  },
  // 初始页面大小
  initialPageSize: {
    type: Number,
    default: 10,
  },
  // 初始页码
  initialPage: {
    type: Number,
    default: 1,
  },
  // 列表项状态映射
  statusMap: {
    type: Object,
    default: () => ({
      ACTIVE: { text: "活跃", type: "success" },
      INACTIVE: { text: "停用", type: "info" },
      PENDING: { text: "待审核", type: "warning" },
      DISABLED: { text: "禁用", type: "danger" },
    }),
  },
});

// 定义事件
const emit = defineEmits(["selection-change", "refresh", "load-data"]);

// 响应式数据
const tableData = ref<any[]>([]);
const total = ref(0);
const loading = ref(false);
const currentPage = ref(props.initialPage);
const pageSize = ref(props.initialPageSize);

// 搜索表单
const searchForm = reactive<Record<string, any>>({});

// 初始化搜索表单默认值
props.searchFields.forEach((field) => {
  searchForm[field.prop] = "";
});

// 将搜索字段转换为 AppForm 需要的格式
const normalizedSearchFields = computed(() => {
  return props.searchFields.map((field) => {
    // 映射类型
    let fieldType: string = field.type || "input";
    if (fieldType === "daterange") {
      fieldType = "daterange";
    } else if (fieldType === "date") {
      fieldType = "date";
    } else if (fieldType === "select") {
      fieldType = "select";
    } else {
      fieldType = "input";
    }

    return {
      prop: field.prop,
      label: field.label,
      type: fieldType,
      options: field.options,
      placeholder: `请输入${field.label}`,
      clearable: true,
    };
  });
});

// 当前排序信息
const currentSort = ref({
  prop: "",
  order: "",
});

// 表格选中项
const selection = ref<any[]>([]);

// 搜索防抖定时器
let searchTimer: NodeJS.Timeout | null = null;

// 计算属性：请求参数
const queryParams = computed(() => {
  const params: any = {
    page: currentPage.value,
    size: pageSize.value,
  };

  // 添加搜索条件
  for (const key in searchForm) {
    if (searchForm[key] !== null && searchForm[key] !== "") {
      params[key] = searchForm[key];
    }
  }

  // 添加排序参数
  if (currentSort.value.prop && currentSort.value.order) {
    params.sort = `${currentSort.value.prop},${
      currentSort.value.order.startsWith("asc") ? "asc" : "desc"
    }`;
  }

  return params;
});

// 获取数据
const loadData = async () => {
  try {
    loading.value = true;
    const response = await props.fetchData(queryParams.value);

    // 根据不同的响应格式处理数据
    if (response && response.data) {
      if (Array.isArray(response.data)) {
        // 直接是数组格式
        tableData.value = response.data;
        total.value = response.data.length;
      } else if (
        response.data.list ||
        response.data.items ||
        response.data.data
      ) {
        // 包含分页信息的对象格式
        tableData.value =
          response.data.list || response.data.items || response.data.data;
        total.value =
          response.data.total ||
          response.data.count ||
          response.data.totalCount ||
          0;
      } else {
        // 其他格式
        tableData.value = [];
        total.value = 0;
      }
    } else {
      tableData.value = [];
      total.value = 0;
    }

    emit("load-data", tableData.value, response);
  } catch (error) {
    console.error("Failed to load table data:", error);
    ElMessage.error("加载数据失败");
    tableData.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
  }
};

// 刷新数据
const refresh = () => {
  currentPage.value = 1;
  loadData();
  emit("refresh");
};

// 处理搜索字段变化
const handleSearchFieldChange = (
  field: string,
  value: any,
  formData: Record<string, any>
) => {
  // 搜索字段变化时延迟触发搜索，避免频繁请求
  if (searchTimer) {
    clearTimeout(searchTimer);
  }
  searchTimer = setTimeout(() => {
    currentPage.value = 1;
    loadData();
  }, 500);
};

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
  loadData();
};

// 处理重置
const handleReset = () => {
  // 重置搜索表单
  for (const key in searchForm) {
    searchForm[key] = "";
  }
  currentPage.value = 1;
  loadData();
};

// 处理页面大小变化
const handleSizeChange = (size: number) => {
  pageSize.value = size;
  currentPage.value = 1;
  loadData();
};

// 处理页码变化
const handleCurrentChange = (page: number) => {
  currentPage.value = page;
  loadData();
};

// 处理表格选中变化
const handleSelectionChange = (val: any[]) => {
  selection.value = val;
  emit("selection-change", val);
};

// 处理排序变化
const handleSortChange = (params: any) => {
  currentSort.value = {
    prop: params.prop,
    order: params.order,
  };
  currentPage.value = 1;
  loadData();
};

// 获取状态标签类型
const getStatusTagType = (status: string) => {
  const statusInfo = props.statusMap[status];
  return statusInfo ? statusInfo.type : "info";
};

// 获取状态文本
const getStatusText = (status: string) => {
  const statusInfo = props.statusMap[status];
  return statusInfo ? statusInfo.text : status;
};

// 初始化加载数据
onMounted(() => {
  loadData();
});

// 监听搜索条件变化
watch(
  searchForm,
  () => {
    // 搜索表单变化时延迟触发搜索，避免频繁请求
    if (searchTimer) {
      clearTimeout(searchTimer);
    }
    searchTimer = setTimeout(() => {
      currentPage.value = 1;
      loadData();
    }, 500);
  },
  { deep: true }
);

// 暴露方法给父组件
defineExpose({
  refresh,
  loadData,
  getSelection: () => selection.value,
  getCurrentPage: () => currentPage.value,
  getPageSize: () => pageSize.value,
  getTotal: () => total.value,
});
</script>

<style lang="scss" scoped>
.app-table {
  .table-search {
    padding: 16px;
    background: #fff;
    border-radius: 4px;
    margin-bottom: 16px;
    box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03);

    .el-form-item {
      margin-bottom: 12px;
      margin-right: 24px;
    }

    .app-form :deep(.el-form) {
      display: flex;
      flex-wrap: wrap;
      align-items: center;

      .el-form-item {
        margin-bottom: 12px;
        margin-right: 24px;
      }

      .form-actions {
        margin-left: auto;
        margin-bottom: 0;
      }
    }

    .table-toolbar {
      display: flex;
      justify-content: space-between;
      margin-bottom: 16px;
      padding: 16px;
      background: #fff;
      border-radius: 4px;
      box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03);

      .toolbar-left {
        display: flex;
        align-items: center;
        gap: 8px;
      }

      .toolbar-right {
        display: flex;
        align-items: center;
        gap: 8px;
      }
    }

    .el-table {
      border-radius: 4px;
    }

    .table-pagination {
      margin-top: 16px;
      text-align: right;
      padding: 16px;
      background: #fff;
      border-radius: 4px;
      box-shadow: 0 1px 2px 0 rgba(0, 0, 0, 0.03);
    }
  }
}
</style>
