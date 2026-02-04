import type { IBase } from '../dto/IBase';
import { request } from '../utils/request'

// 权限相关接口
export interface IPermission {
  id?: number;
  tenantId: number;
  parentId: number;
  permCode: string;
  permName: string;
  permType: number;
  resourcePath: string;
  icon?: string;
  sort: number;
  status: number;
  creator?: string;
  createTime?: string;
  updater?: string;
  updateTime?: string;
}

export interface IPermissionQuery {
  page: number;
  size: number;
  sort?: string[];
}

// 分页查询权限
export const getPermissions = async (params: IPermissionQuery) => {
  const res = await request.get<IBase<any>>('/api/permissions', { params });
  return res.data;
};

// 根据ID获取权限
export const getPermissionById = async (id: number) => {
  const res = await request.get<IBase<IPermission>>(`/api/permissions/${id}`);
  return res.data;
};

// 创建权限
export const createPermission = async (data: IPermission) => {
  const res = await request.post<IBase<IPermission>>('/api/permissions', data);
  return res.data;
};

// 更新权限
export const updatePermission = async (id: number, data: IPermission) => {
  const res = await request.put<IBase<IPermission>>(`/api/permissions/${id}`, data);
  return res.data;
};

// 删除权限
export const deletePermission = async (id: number) => {
  const res = await request.delete<IBase<void>>(`/api/permissions/${id}`);
  return res.data;
};

// 根据租户查询权限
export const getPermissionsByTenant = async (tenantId: number) => {
  const res = await request.get<IBase<IPermission[]>>(`/api/permissions/tenant/${tenantId}`);
  return res.data;
};

// 根据角色查询权限
export const getPermissionsByRole = async (roleId: number) => {
  const res = await request.get<IBase<IPermission[]>>(`/api/permissions/role/${roleId}`);
  return res.data;
};