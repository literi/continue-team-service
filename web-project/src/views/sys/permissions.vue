<template>
  <div class="permissions-page">
    <app-table
      :fetch-data="fetchPermissions"
      :columns="columns"
      :search-fields="searchFields"
      @selection-change="handleSelectionChange"
    >
      <template #operation="{ row }">
        <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
      </template>
      
      <template #toolbar-left>
        <el-button type="primary" @click="handleAdd">新增权限</el-button>
      </template>
    </app-table>
    
    <!-- 编辑/新增弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
    >
      <app-form
        :fields="formFields"
        v-model="formData"
        :show-actions="false"
        ref="formRef"
      />
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getPermissions, createPermission, updatePermission, deletePermission } from '@/api/permission';
import AppTable from '../../components/AppTable.vue';
import AppForm from '../../components/AppForm.vue';
import type { TableColumn } from '@/dto/ITable';

// 表格列配置
const columns: TableColumn[] = [
  { prop: 'id', label: 'ID', width: 80 },
  { prop: 'permCode', label: '权限编码', minWidth: 120 },
  { prop: 'permName', label: '权限名称', minWidth: 150 },
  { prop: 'permType', label: '权限类型', minWidth: 100 },
  { prop: 'resourcePath', label: '资源路径', minWidth: 150 },
  { prop: 'status', label: '状态', type: 'status', minWidth: 100 },
  { prop: 'sort', label: '排序', width: 80 },
  { prop: 'createTime', label: '创建时间', minWidth: 150 },
];

// 搜索字段配置
const searchFields = [
  { prop: 'permCode', label: '权限编码', type: 'input' },
  { prop: 'permName', label: '权限名称', type: 'input' },
];

// 表单字段配置
const formFields = [
  { prop: 'tenantId', label: '租户ID', type: 'number', rules: [{ required: true, message: '请输入租户ID', trigger: 'blur' }] },
  { prop: 'parentId', label: '父级权限', type: 'number' },
  { prop: 'permCode', label: '权限编码', type: 'input', rules: [{ required: true, message: '请输入权限编码', trigger: 'blur' }] },
  { prop: 'permName', label: '权限名称', type: 'input', rules: [{ required: true, message: '请输入权限名称', trigger: 'blur' }] },
  { prop: 'permType', label: '权限类型', type: 'select', options: [
    { label: '菜单', value: 1 },
    { label: '按钮', value: 2 },
    { label: '接口', value: 3 }
  ], rules: [{ required: true, message: '请选择权限类型', trigger: 'change' }] },
  { prop: 'resourcePath', label: '资源路径', type: 'input' },
  { prop: 'icon', label: '图标', type: 'input' },
  { prop: 'sort', label: '排序', type: 'number' },
  { prop: 'status', label: '状态', type: 'select', options: [
    { label: '启用', value: 1 },
    { label: '禁用', value: 0 }
  ], rules: [{ required: true, message: '请选择状态', trigger: 'change' }] },
];

// 响应式数据
const dialogVisible = ref(false);
const dialogTitle = ref('');
const submitting = ref(false);
const currentRow = ref(null);

// 表单数据
const formData = reactive({
  id: undefined,
  tenantId: 1,
  parentId: 0,
  permCode: '',
  permName: '',
  permType: 1,
  resourcePath: '',
  icon: '',
  sort: 0,
  status: 1,
});

// 获取数据
const fetchPermissions = async (params: any) => {
  return await getPermissions(params);
};

// 处理新增
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    tenantId: 1,
    parentId: 0,
    permCode: '',
    permName: '',
    permType: 1,
    resourcePath: '',
    icon: '',
    sort: 0,
    status: 1,
  });
  dialogTitle.value = '新增权限';
  dialogVisible.value = true;
  currentRow.value = null;
};

// 处理编辑
const handleEdit = (row: any) => {
  Object.assign(formData, { ...row });
  dialogTitle.value = '编辑权限';
  dialogVisible.value = true;
  currentRow.value = row;
};

// 处理删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确认删除权限 "${row.permName}" 吗？`, '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    });
    
    await deletePermission(row.id);
    ElMessage.success('删除成功');
    // 通过emit事件通知父组件刷新，这里暂时使用页面刷新
    location.reload();
  } catch (error) {
    console.error('删除失败:', error);
  }
};

// 处理提交
const handleSubmit = async () => {
  try {
    if (formData.id) {
      // 更新权限
      await updatePermission(formData.id, formData);
      ElMessage.success('更新成功');
    } else {
      // 创建权限
      await createPermission(formData);
      ElMessage.success('创建成功');
    }
    dialogVisible.value = false;
    // 通知表格刷新数据
    location.reload(); // 简单的刷新页面方式
  } catch (error) {
    console.error('操作失败:', error);
    ElMessage.error('操作失败');
  }
};

// 处理表格选中变化
const handleSelectionChange = (selection: any[]) => {
  console.log('选中项变化:', selection);
};
</script>

<style scoped>
.permissions-page {
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}
</style>