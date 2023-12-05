package cn.lyn4ever.export2md.service;

import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Post;
import java.io.File;

/**
 * @author Lyn4ever29
 * @url https://jhacker.cn
 * @date 2023/11/12
 */
public interface ImportService {


    /**
     * 运行导出任务
     *
     * @param filePart
     */
    Mono<Post> runTask(File file);

}



