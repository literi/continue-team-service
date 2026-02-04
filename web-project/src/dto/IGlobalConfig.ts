

export interface IMenuItem  {
    id: number;
    icon: string | null;
    parentId: number;
    permCode: string;
    permName: string;
    resourcePath: string;
    sort: number;
    status: number;
    children?: IMenuItem[];
}
export interface IUserInfo {
    createTime: string;
    email: string;
    id: number;
    mobile: string;
    realName: string;
    status: number;
    tenantId: number;
    updateTime: string;
    username: string;
}
export interface IGlobalConfig {
    theme: string;
    asideStatus: boolean;
    menuList: IMenuItem[];
    userInfo: IUserInfo | null;
    dynamicRoutes?: string[];
    activeTab: string;
    menuTabs: any[];
}
