package cn.lyn4ever.plugin.rest;

import cn.lyn4ever.plugin.schema.ExportLogSchema;
import cn.lyn4ever.plugin.service.ExportService;
import cn.lyn4ever.plugin.util.FileUtil;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.plugin.ApiVersion;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;

/**
 * 自定义导出接口
 *
 * @author Lyn4ever29
 * @url https://jhacker.cn
 * @date 2023/11/1
 */
@ApiVersion("v1alpha1")
@RequestMapping("/doExport")
@RestController
@Slf4j
public class ExportController {
// /apis/plugin.api.halo.run/v1alpha1/plugins/export-anything/doExport/**

    @Autowired
    private ExportService exportService;

    @Autowired
    private ReactiveExtensionClient reactiveClient;


    @PostMapping("/export")
    public Mono<ExportLogSchema> export(@RequestBody final ExportLogSchema exportLogSchema) {
        exportLogSchema.setCreateTime(new Date());
        exportLogSchema.setStatus("b");
        //设置元数据才能保存
        exportLogSchema.setMetadata(new Metadata());
        exportLogSchema.getMetadata().setName(exportLogSchema.getName());


        return reactiveClient.create(exportLogSchema).doOnSuccess(
                exportLogSchema1 -> {
                    Thread t = new Thread(() -> {
                        exportService.runTask(exportLogSchema);
                    });
                    t.start();
                }
        );
    }


    @PostMapping("/del")
    public Mono<Void> delete(@RequestBody String names) {

        Arrays.stream(names.split(",")).parallel().forEach(name ->
                reactiveClient.fetch(ExportLogSchema.class, name)
                        .doOnSuccess(exportLogSchema -> reactiveClient.delete(exportLogSchema)).subscribe());

        return Mono.empty();
    }

    @GetMapping("/down/{path}")
    public Mono<ServerResponse> down(@PathVariable("path") String path, ServerHttpResponse response) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(path)) {
            return Mono.empty();
        }

        Path docFile = FileUtil.getDocFile();
        File file = new File(docFile.toFile().getAbsolutePath() + "/" + path + ".zip");
        System.out.println(file.getAbsolutePath());
        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }
        System.out.println("================");


        ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
        HttpHeaders headers = zeroCopyResponse.getHeaders();
        headers.set("Content-Disposition", "attachment; filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
        headers.set("file-name", URLEncoder.encode(path, "UTF-8"));
        headers.set("Access-Control-Allow-Origin", "*");
        MediaType application = new MediaType("application", "octet-stream", StandardCharsets.UTF_8);
        headers.setContentType(application);
        zeroCopyResponse.writeWith(file, 0, file.length()).subscribe();

        return null;
    }


    /**
     * 文件上传
     *
     * @param file
     * @return
     * @throws IOException
     * @throws InvalidFormatException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public Mono<Void> exportImport(@RequestPart("file") FilePart file) throws IOException, InvalidFormatException, IllegalAccessException, InstantiationException {
        File file1 = new File(file.filename());
        file.transferTo(file1);
        return null;
    }
}
