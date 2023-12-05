package cn.lyn4ever.export2md.service;

import cn.lyn4ever.export2md.schema.ExportLogSchema;

/**
 * @author Lyn4ever29
 * @url https://jhacker.cn
 * @date 2023/12/5
 */
public interface ExportService {
    public void runTask(ExportLogSchema exportLogSchema);

}
