<template>
  <div class="roles-page">
    <app-table
      :fetch-data="fetchRoles"
      :columns="columns"
      :search-fields="searchFields"
      @selection-change="handleSelectionChange"
    >
      <template #operation="{ row }">
        <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
      </template>
      
      <template #toolbar-left>
        <el-button type="primary" @click="handleAdd">新增角色</el-button>
      </template>
    </app-table>
    
    <!-- 编辑/新增弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
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
import { getRoles, createRole, updateRole, deleteRole } from '../../api/role';
import AppTable from '../../components/AppTable.vue';
import AppForm from '../../components/AppForm.vue';
import type { TableColumn } from '@/dto/ITable';

// 表格列配置
const columns: TableColumn[] = [
  { prop: 'id', label: 'ID', width: 80 },
  { prop: 'roleCode', label: '角色编码', minWidth: 120 },
  { prop: 'roleName', label: '角色名称', minWidth: 150 },
  { prop: 'status', label: '状态', type: 'status', minWidth: 100 },
  { prop: 'sort', label: '排序', width: 80 },
  { prop: 'createTime', label: '创建时间', minWidth: 150 },
];

// 搜索字段配置
const searchFields = [
  { prop: 'roleCode', label: '角色编码', type: 'input' },
  { prop: 'roleName', label: '角色名称', type: 'input' },
];

// 表单字段配置
const formFields = [
  { prop: 'tenantId', label: '租户ID', type: 'number', rules: [{ required: true, message: '请输入租户ID', trigger: 'blur' }] },
  { prop: 'roleCode', label: '角色编码', type: 'input', rules: [{ required: true, message: '请输入角色编码', trigger: 'blur' }] },
  { prop: 'roleName', label: '角色名称', type: 'input', rules: [{ required: true, message: '请输入角色名称', trigger: 'blur' }] },
  { prop: 'status', label: '状态', type: 'select', options: [
    { label: '启用', value: 1 },
    { label: '禁用', value: 0 }
  ], rules: [{ required: true, message: '请选择状态', trigger: 'change' }] },
  { prop: 'sort', label: '排序', type: 'number' },
  { prop: 'remark', label: '备注', type: 'textarea' },
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
  roleCode: '',
  roleName: '',
  status: 1,
  sort: 0,
  remark: '',
});

// 获取数据
const fetchRoles = async (params: any) => {
  return await getRoles(params);
};

// 处理新增
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    tenantId: 1,
    roleCode: '',
    roleName: '',
    status: 1,
    sort: 0,
    remark: '',
  });
  dialogTitle.value = '新增角色';
  dialogVisible.value = true;
  currentRow.value = null;
};

// 处理编辑
const handleEdit = (row: any) => {
  Object.assign(formData, { ...row });
  dialogTitle.value = '编辑角色';
  dialogVisible.value = true;
  currentRow.value = row;
};

// 处理删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确认删除角色 "${row.roleName}" 吗？`, '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    });
    
    await deleteRole(row.id);
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
      // 更新角色
      await updateRole(formData.id, formData);
      ElMessage.success('更新成功');
    } else {
      // 创建角色
      await createRole(formData);
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
.roles-page {
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}
</style>