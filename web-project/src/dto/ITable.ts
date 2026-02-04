/**
 * 表格列配置接口
 */
export interface TableColumn {
  prop: string;
  label: string;
  width?: number | string;
  minWidth?: number | string;
  fixed?: boolean | "left" | "right";
  sortable?: boolean | "custom";
  type?: "normal" | "status" | "expand" | "selection";
  formatter?: (row: any, column: any, cellValue: any, index: number) => any;
  slotName?: string;
  [key: string]: any;
}

/**
 * 搜索字段接口
 */
export interface SearchField {
  prop: string;
  label: string;
  type?: "input" | "select" | "date" | "daterange";
  options?: Array<{ label: string; value: any }>;
}

/**
 * 表格配置接口
 */
export interface TableConfig {
  columns: TableColumn[];
  searchFields?: SearchField[];
  showSearch?: boolean;
  showPagination?: boolean;
  border?: boolean;
  stripe?: boolean;
  rowKey?: string;
}