import type { IBase } from '../dto/IBase';
import { request } from '../utils/request'

// 用户相关接口
export interface IUser {
  id?: number;
  tenantId: number;
  username: string;
  password?: string;
  realName: string;
  mobile?: string;
  email?: string;
  status: number;
  creator?: string;
  createTime?: string;
  updater?: string;
  updateTime?: string;
}

export interface IUserQuery {
  page: number;
  size: number;
  sort?: string[];
}

// 分页查询用户
export const getUsers = async (params: IUserQuery) => {
  const res = await request.get<IBase<any>>('/api/users', { params });
  return res.data;
};

// 根据ID获取用户
export const getUserById = async (id: number) => {
  const res = await request.get<IBase<IUser>>(`/api/users/${id}`);
  return res.data;
};

// 创建用户
export const createUser = async (data: IUser) => {
  const res = await request.post<IBase<IUser>>('/api/users', data);
  return res.data;
};

// 更新用户
export const updateUser = async (id: number, data: IUser) => {
  const res = await request.put<IBase<IUser>>(`/api/users/${id}`, data);
  return res.data;
};

// 删除用户
export const deleteUser = async (id: number) => {
  const res = await request.delete<IBase<void>>(`/api/users/${id}`);
  return res.data;
};

// 为用户分配角色
export const assignRoleToUser = async (userId: number, roleId: number, tenantId: number) => {
  const res = await request.post<IBase<void>>(`/api/users/${userId}/roles/${roleId}?tenantId=${tenantId}`);
  return res.data;
};

// 移除用户角色
export const removeRoleFromUser = async (userId: number, roleId: number) => {
  const res = await request.delete<IBase<void>>(`/api/users/${userId}/roles/${roleId}`);
  return res.data;
};

// 根据租户查询用户
export const getUsersByTenant = async (tenantId: number) => {
  const res = await request.get<IBase<IUser[]>>(`/api/users/tenant/${tenantId}`);
  return res.data;
};