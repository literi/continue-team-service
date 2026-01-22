import type { IBase } from '../dto/IBase';
import type { IMenuItem, IUserInfo } from '../dto/IGlobalConfig';
import { request } from '../utils/request'

export const getMenuList = async () => {
  const res = await request.get<IBase<IMenuItem[]>>('/api/profile/info');
  return res.data;
}

export const getUserInfo = async () => {
  const res = await request.get<IBase<IUserInfo>>('/api/profile/menu');
  return res.data;
}
