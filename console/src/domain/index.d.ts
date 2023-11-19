
/**
 *
 * @export
 * @interface ExportLog
 */
export interface ExportLog {
  name: string;
  costSeconds: number;
  createTime: Date;
  status: string;
}

export interface ProblemDetail {
  detail: string;
  instance: string;
  status: number;
  title: string;
  type?: string;
}
