<template>
  <div class="users-page">
    <app-table
      :fetch-data="fetchUsers"
      :columns="columns"
      :search-fields="searchFields"
      @selection-change="handleSelectionChange"
    >
      <template #operation="{ row }">
        <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
        <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
      </template>
      
      <template #toolbar-left>
        <el-button type="primary" @click="handleAdd">新增用户</el-button>
      </template>
    </app-table>
    
    <!-- 编辑/新增弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
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
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getUsers, createUser, updateUser, deleteUser } from '@/api/user';
import AppTable from '@/components/AppTable.vue';
import AppForm from '@/components/AppForm.vue';

// 表格列配置
const columns = [
  { prop: 'id', label: 'ID', width: 80 },
  { prop: 'username', label: '用户名', minWidth: 120 },
  { prop: 'realName', label: '真实姓名', minWidth: 120 },
  { prop: 'mobile', label: '手机号', minWidth: 120 },
  { prop: 'email', label: '邮箱', minWidth: 150 },
  { prop: 'status', label: '状态', type: 'status', minWidth: 100 },
];

// 搜索字段配置
const searchFields = [
  { prop: 'username', label: '用户名', type: 'input' },
  { prop: 'realName', label: '真实姓名', type: 'input' },
];

// 表单字段配置
const formFields = [
  { prop: 'tenantId', label: '租户ID', type: 'number', rules: [{ required: true, message: '请输入租户ID', trigger: 'blur' }] },
  { prop: 'username', label: '用户名', type: 'input', rules: [{ required: true, message: '请输入用户名', trigger: 'blur' }] },
  { prop: 'realName', label: '真实姓名', type: 'input', rules: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }] },
  { prop: 'mobile', label: '手机号', type: 'input' },
  { prop: 'email', label: '邮箱', type: 'input' },
  { prop: 'status', label: '状态', type: 'select', options: [
    { label: '启用', value: 1 },
    { label: '禁用', value: 0 }
  ], rules: [{ required: true, message: '请选择状态', trigger: 'change' }] },
  { prop: 'password', label: '密码', type: 'input', rules: [{ required: false, message: '请输入密码', trigger: 'blur' }] },
];

// 响应式数据
const dialogVisible = ref(false);
const dialogTitle = ref('');
const submitting = ref(false);
const formRef = ref();
const currentRow = ref(null);

// 表单数据
const formData = reactive({
  id: undefined,
  tenantId: 1, // 默认租户ID
  username: '',
  realName: '',
  mobile: '',
  email: '',
  status: 1,
  password: '',
});

// 获取数据
const fetchUsers = async (params: any) => {
  return await getUsers(params);
};

// 处理新增
const handleAdd = () => {
  Object.assign(formData, {
    id: undefined,
    tenantId: 1,
    username: '',
    realName: '',
    mobile: '',
    email: '',
    status: 1,
    password: '',
  });
  dialogTitle.value = '新增用户';
  dialogVisible.value = true;
  currentRow.value = null;
};

// 处理编辑
const handleEdit = (row: any) => {
  Object.assign(formData, { ...row });
  dialogTitle.value = '编辑用户';
  dialogVisible.value = true;
  currentRow.value = row;
};

// 处理删除
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确认删除用户 "${row.username}" 吗？`, '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    });
    
    await deleteUser(row.id);
    ElMessage.success('删除成功');
    // 刷新表格
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
      // 更新用户
      await updateUser(formData.id, formData);
      ElMessage.success('更新成功');
    } else {
      // 创建用户
      await createUser(formData);
      ElMessage.success('创建成功');
    }
    dialogVisible.value = false;
    // 通知表格刷新数据
    window.location.reload(); // 简单的刷新页面方式
  } catch (error) {
    console.error('操作失败:', error);
    ElMessage.error('操作失败');
  }
};

// 处理表格选中变化
const handleSelectionChange = (selection: any[]) => {
  console.log('选中项变化:', selection);
};

onMounted(() => {
  console.log('Users page mounted');
});
</script>

<style scoped>
.users-page {
  padding: 20px;
  background: #f5f5f5;
  min-height: 100vh;
}
</style>