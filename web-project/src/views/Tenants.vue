<template>
  <div class="tenants-page">
    <app-table
      :fetch-data="fetchTenants"
      :columns="columns"
      :search-fields="searchFields"
      @selection-change="handleSelectionChange"
    >
      <template #operation="{ row }">
        <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
      </template>
      
      <template #toolbar-left>
        <el-button type="primary" @click="handleAdd">新增租户</el-button>
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
import { getTenants, createTenant, updateTenant, deleteTenant } from '@/api/tenant';
import AppTable from '@/components/AppTable.vue';
import AppForm from '@/components/AppForm.vue';

// 表格列配置
const columns = [
  { prop: 'id', label: 'ID', width: 80 },
  { prop: 'tenantCode', label: '租户编码', minWidth: 120 },
  { prop: 'tenantName', label: '租户名称', minWidth: 150 },
  { prop: 'status', label: '状态', type: 'status', minWidth: 100 },
  { prop: 'createTime', label: '创建时间', minWidth: 150 },
];

// 搜索字段配置
const searchFields = [
  { prop: 'tenantCode', label: '租户编码', type: 'input' },
  { prop: 'tenantName', label: '租户名称', type: 'input' },
];

// 表单字段配置
const formFields = [
  { prop: 'tenantCode', label: '租户编码', type: 'input', rules: [{ required: true, message: '请输入租户编码', trigger: 'blur' }] },
  { prop: 'tenantName', label: '租户名称', type: 'input', rules: [{ required: true, message: '请输入租户名称', trigger: 'blur' }] },
  { prop: 'status', label: '状态', type: 'select', options: [
    { label: '启用', value: 1 },
    { label: '禁用', value: 0 }
  ], rules: [{ required: true, message: '请选择状态', trigger: 'change' }] },
  { prop: 'expireTime', label: '过期时间', type: 'date' },
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
  tenantCode: '',
  tenantName: '',
  status: 1,
  expireTime: '',
  remark: '',
});

// 获取数据
const fetchTenants = async (params: any) => {
  return await getTenants(params);
};

// 处理新增
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    tenantCode: '',
    tenantName: '',
    status: 1,
    expireTime: '',
    remark: '',
  });
  dialogTitle.value = '新增租户';
  dialogVisible.value = true;
  currentRow.value = null;
};

// 处理编辑
const handleEdit = (row: any) => {
  Object.assign(formData, { ...row });
  dialogTitle.value = '编辑租户';
  dialogVisible.value = true;
  currentRow.value = row;
};

// 处理删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确认删除租户 "${row.tenantName}" 吗？`, '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    });
    
    await deleteTenant(row.id);
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
      // 更新租户
      await updateTenant(formData.id, formData);
      ElMessage.success('更新成功');
    } else {
      // 创建租户
      await createTenant(formData);
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
.tenants-page {
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}
</style>