

export interface IMenuItem  {
    id: number;
    name: string;
    path: string;
    children?: IMenuItem[];
}
export interface IUserInfo {
    id: number;
    username: string;
    initials: string;
    avatar: string;
}
export interface IGlobalConfig {
    theme: string;
    asideStatus: boolean;
    menuList: IMenuItem[];
    userInfo: IUserInfo | null;
}
