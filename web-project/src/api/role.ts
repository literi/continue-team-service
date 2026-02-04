import type { IBase } from '../dto/IBase';
import { request } from '../utils/request'

// 角色相关接口
export interface IRole {
  id?: number;
  tenantId: number;
  roleCode: string;
  roleName: string;
  status: number;
  sort: number;
  creator?: string;
  createTime?: string;
  updater?: string;
  updateTime?: string;
  remark?: string;
}

export interface IRoleQuery {
  page: number;
  size: number;
  sort?: string[];
}

// 分页查询角色
export const getRoles = async (params: IRoleQuery) => {
  const res = await request.get<IBase<any>>('/api/roles', { params });
  return res.data;
};

// 根据ID获取角色
export const getRoleById = async (id: number) => {
  const res = await request.get<IBase<IRole>>(`/api/roles/${id}`);
  return res.data;
};

// 创建角色
export const createRole = async (data: IRole) => {
  const res = await request.post<IBase<IRole>>('/api/roles', data);
  return res.data;
};

// 更新角色
export const updateRole = async (id: number, data: IRole) => {
  const res = await request.put<IBase<IRole>>(`/api/roles/${id}`, data);
  return res.data;
};

// 删除角色
export const deleteRole = async (id: number) => {
  const res = await request.delete<IBase<void>>(`/api/roles/${id}`);
  return res.data;
};

// 为角色分配权限
export const assignPermissionToRole = async (roleId: number, permId: number, tenantId: number) => {
  const res = await request.post<IBase<void>>(`/api/roles/${roleId}/permissions/${permId}?tenantId=${tenantId}`);
  return res.data;
};

// 移除角色权限
export const removePermissionFromRole = async (roleId: number, permId: number) => {
  const res = await request.delete<IBase<void>>(`/api/roles/${roleId}/permissions/${permId}`);
  return res.data;
};

// 根据租户查询角色
export const getRolesByTenant = async (tenantId: number) => {
  const res = await request.get<IBase<IRole[]>>(`/api/roles/tenant/${tenantId}`);
  return res.data;
};