import type { IBase } from '../dto/IBase'
import type { ICaptcha } from '../dto/ICaptcha'
import type { ILogin } from '../dto/ILogin';
import { request } from '../utils/request'
export const getCaptcha = async () => {
  const res = await request.get<IBase<ICaptcha>>('/api/v1/auth/captcha');
  return res.data;
}


export const userLogin = async (payload: ILogin) => {
  const res = await request.post<IBase<any>>('/api/v1/auth/login', payload);
  return res.data;
}
