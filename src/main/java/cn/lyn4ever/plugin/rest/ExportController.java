package cn.lyn4ever.plugin.rest;

import cn.lyn4ever.plugin.schema.ExportLogSchema;
import cn.lyn4ever.plugin.service.ExportService;
import cn.lyn4ever.plugin.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.plugin.ApiVersion;

import java.io.File;
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

    private final String EXPORT_ONE_DIR = "markdown_post";

    @Autowired
    private ExportService exportService;

    @Autowired
    private ReactiveExtensionClient reactiveClient;
    @Autowired
    private ExtensionClient commonClient;


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

    /**
     * 导出单篇文章
     *
     * @param name
     * @return
     */
    @GetMapping("/export_one/{name}")
    public Mono<Void> fetchHeadContent(@PathVariable("name") String name, ServerHttpResponse response) throws UnsupportedEncodingException {

        return Mono.fromCallable(() -> {
                    //写文件
                    String fileName = exportService.writePost(name, EXPORT_ONE_DIR);
                    File file = new File(fileName);

                    ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
                    HttpHeaders headers = zeroCopyResponse.getHeaders();
                    headers.set("Content-Disposition", "attachment; filename=" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8));
                    headers.set("file-name", URLEncoder.encode(file.getName(), StandardCharsets.UTF_8));
                    headers.set("Access-Control-Allow-Origin", "*");
                    MediaType application = new MediaType("application", "octet-stream", StandardCharsets.UTF_8);
                    headers.setContentType(application);
                    zeroCopyResponse.writeWith(file, 0, file.length()).subscribe();
                    return "";
                })
                .publishOn(Schedulers.boundedElastic()).then();


    }

    @PostMapping("/del")
    public Mono<Void> delete(@RequestBody String[] names) {

        Arrays.stream(names).parallel().forEach(name ->
                reactiveClient.fetch(ExportLogSchema.class, name)
                        .publishOn(Schedulers.boundedElastic())
                        .doOnSuccess(exportLogSchema -> {
                            reactiveClient.delete(exportLogSchema).doOnSuccess(exportLogSchema1 -> {
                                //删除文件
                                exportService.delete(name);
                            }).subscribe();
                        }).subscribe());
        return Mono.empty();
    }

    @GetMapping("/down/{path}")
    public Mono<Void> down(@PathVariable("path") String path, ServerHttpResponse response) {
        if (StringUtils.isBlank(path)) {
            return Mono.empty();
        }
        if (path.split("\\.").length > 2) {
            //包含太多.的路径，不可下载
            return Mono.empty();
        }

        Path docFile = FileUtil.getDocFile(FileUtil.DirPath.EXPORT);
        File file = new File(docFile.toFile().getAbsolutePath() + "/" + path + ".zip");
        if (!file.exists()) {
            //todo 适配旧版本,未来会删除
            file = new File(docFile.toFile().getAbsolutePath() + "/../" + path + ".zip");
            if (!file.exists()) {
                throw new RuntimeException("文件不存在");
            }
        }

        ZeroCopyHttpOutputMessage zeroCopyResponse = (ZeroCopyHttpOutputMessage) response;
        HttpHeaders headers = zeroCopyResponse.getHeaders();
        headers.set("Content-Disposition", "attachment; filename=" + URLEncoder.encode(file.getName(), StandardCharsets.UTF_8));
        headers.set("file-name", URLEncoder.encode(path, StandardCharsets.UTF_8));
        headers.set("Access-Control-Allow-Origin", "*");
        MediaType application = new MediaType("application", "octet-stream", StandardCharsets.UTF_8);
        headers.setContentType(application);
        zeroCopyResponse.writeWith(file, 0, file.length()).subscribe();

        return Mono.empty();
    }


}
