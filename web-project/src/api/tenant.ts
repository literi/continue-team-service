import type { IBase } from '../dto/IBase';
import { request } from '../utils/request'

// 租户相关接口
export interface ITenant {
  id?: number;
  tenantCode: string;
  tenantName: string;
  status: number;
  expireTime?: string;
  creator?: string;
  createTime?: string;
  updater?: string;
  updateTime?: string;
  remark?: string;
}

export interface ITenantQuery {
  page: number;
  size: number;
  sort?: string[];
}

// 分页查询租户
export const getTenants = async (params: ITenantQuery) => {
  const res = await request.get<IBase<any>>('/api/tenants', { params });
  return res.data;
};

// 根据ID获取租户
export const getTenantById = async (id: number) => {
  const res = await request.get<IBase<ITenant>>(`/api/tenants/${id}`);
  return res.data;
};

// 创建租户
export const createTenant = async (data: ITenant) => {
  const res = await request.post<IBase<ITenant>>('/api/tenants', data);
  return res.data;
};

// 更新租户
export const updateTenant = async (id: number, data: ITenant) => {
  const res = await request.put<IBase<ITenant>>(`/api/tenants/${id}`, data);
  return res.data;
};

// 删除租户
export const deleteTenant = async (id: number) => {
  const res = await request.delete<IBase<void>>(`/api/tenants/${id}`);
  return res.data;
};

// 根据租户编码查询
export const getTenantByCode = async (tenantCode: string) => {
  const res = await request.get<IBase<ITenant>>(`/api/tenants/code/${tenantCode}`);
  return res.data;
};