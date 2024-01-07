import type { Metadata } from "@halo-dev/api-client";
/**
 *
 * @export
 * @interface ExportLog
 */
export interface ExportLog {
  name: string;
  costSeconds: number;
  createTime: Date;
  status: "a" | "b" | "c";
  tag: string;
  category: string;
  beginTime: string;
  endTime: string;
  remainMetaData: boolean;
  remainCategory: boolean;
  kind: "ExportLog";
  apiVersion: "cn.lyn4ever.export2doc/v1alpha1";
  metadata: Metadata;
}

export interface ProblemDetail {
  detail: string;
  instance: string;
  status: number;
  title: string;
  type?: string;
}

export interface ListedExportLogList {
  first: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
  items: Array<ExportLog>;
  last: boolean;
  page: number;
  size: number;
  total: number;
  totalPages: number;
}
