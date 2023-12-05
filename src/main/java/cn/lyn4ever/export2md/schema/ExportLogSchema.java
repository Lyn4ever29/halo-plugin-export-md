package cn.lyn4ever.export2md.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

/**
 * @author Lyn4ever29
 * @url <a href="https://jhacker.cn">...</a>
 * @date 2023/11/1
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@GVK(kind = "ExportLog", group = "cn.lyn4ever.export2doc",
    version = "v1alpha1", singular = "exportLog", plural = "exportLogs")
public class ExportLogSchema extends AbstractExtension {
    @Schema
    private String name = LocalDateTime.now().toString();
    @Schema
    private Date createTime = new Date();
    @Schema
    private Long costSeconds;
    @Schema
    private String tag;
    @Schema
    private String category;

    @Schema
    private String beginTime;

    @Schema
    private String endTime;

    @Schema(defaultValue = "true")
    private Boolean remainMetaData;

    @Schema(defaultValue = "true")
    private Boolean remainCategory;

    /**
     * 状态
     * a-失败
     * b-导出中
     * c-成功
     */
    @Schema
    private String status;

    // @Schema(requiredMode = REQUIRED)
    // private ExportConfigSchema.TodoSpec spec;
    //
    // @Data
    // public static class TodoSpec {
    //
    //     @Schema(requiredMode = REQUIRED, minLength = 1)
    //     private String title;
    //
    //     @Schema(defaultValue = "false")
    //     private Boolean done;
    // }
}
