package cn.lyn4ever.export2md.schema;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.GVK;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Lyn4ever29
 * @url <a href="https://jhacker.cn">...</a>
 * @date 2023/11/1
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
@GVK(kind = "ImportLog", group = "cn.lyn4ever.export2doc",
    version = "v1alpha1", singular = "importLog", plural = "importLogs")
public class ImportLogSchema extends AbstractExtension {
    @Schema
    private String name = LocalDateTime.now().toString();
    @Schema
    private Date createTime = new Date();
    @Schema
    private Long costSeconds;
    @Schema
    private String title;
    /**
     * 文章name
     */
    @Schema
    private String postName;

    /**
     * 状态
     * a-失败
     * b-导出中
     * c-成功
     */
    @Schema
    private String status;
}
