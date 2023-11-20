package cn.lyn4ever.plugin.rest;

import cn.lyn4ever.plugin.service.ExportService;
import cn.lyn4ever.plugin.service.ImportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.plugin.ApiVersion;

/**
 * 自定义导入接口
 *
 * @author Lyn4ever29
 * @url https://jhacker.cn
 * @date 2023/11/1
 */
@ApiVersion("v1alpha1")
@RequestMapping("/doImport")
@RestController
@Slf4j
public class ImportController {
// /apis/api.plugin.halo.run/v1alpha1/plugins/export2doc/doImport/**

    @Autowired
    private ExportService exportService;
    @Autowired
    private ImportService importService;

    @Autowired
    private ReactiveExtensionClient reactiveClient;


    @PostMapping(value = "/import", consumes = {
            MediaType.TEXT_MARKDOWN_VALUE,
            MediaType.TEXT_EVENT_STREAM_VALUE,
            "text/*",
            MediaType.APPLICATION_PROBLEM_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public Mono<String> export(@RequestPart("file") final Flux<FilePart> filePartFlux) {

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName)
                .publishOn(Schedulers.boundedElastic())
                .flatMap(currentUserName -> {
                    filePartFlux.flatMap(it -> {
                        new Thread(() -> {
                            importService.runTask(it,currentUserName);
                        }).start();
                        return Mono.just(it);
                    }).subscribe();
                    return Mono.just(currentUserName);
                }).then(Mono.just("OK"));
    }


//        return flux.flatMap(map->{
//            List<FilePart> fileParts = map.get("file");
//            fileParts.forEach(it->{
//                new Thread(() -> {
//                    importService.runTask(it);
//                }).start();
//            });
//            return null;
//        }).then(Mono.just("OK"));


}
