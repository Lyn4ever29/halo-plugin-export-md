package cn.lyn4ever.plugin.rest;

import cn.hutool.core.util.ZipUtil;
import cn.lyn4ever.plugin.dto.ContentWrapper;
import cn.lyn4ever.plugin.schema.ExportLogSchema;
import cn.lyn4ever.plugin.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import run.halo.app.core.extension.content.Post;
import run.halo.app.core.extension.content.Snapshot;
import run.halo.app.extension.*;
import run.halo.app.plugin.ApiVersion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;

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

    private final int pageSize = 20;

    private final Map<String, String> extendNameMap = new HashMap<>() {{
        put("markdown", ".md");
        put("html", ".html");
        put("json", ".josn");

    }};


    @Autowired
    private ReactiveExtensionClient client;
    private ExtensionClient client;


    @PostMapping("/export")
    public Object export(@RequestBody ExportLogSchema exportLogSchema) {
        long old = System.currentTimeMillis();
        //根据配置获取对应的文章
        String categories = exportLogSchema.getCategory();
        String tag = exportLogSchema.getTag();

        Predicate<Post> paramPredicate = post -> true;
//        if (StringUtils.isNotBlank(categories)) {
//            paramPredicate.and(post -> contains(Set.copyOf(List.of(categories.split(","))), post.getSpec().getCategories()));
//        }
//        if (StringUtils.isNotBlank(tag)) {
//            paramPredicate.and(post -> contains(Set.copyOf(List.of(tag.split(","))), post.getSpec().getTags()));
//        }


        Mono<ListResult<Post>> res = client.list(Post.class, paramPredicate, null, 1, pageSize);
        System.out.println("===================");
        System.out.println(exportLogSchema);
        System.out.println("===================");

        exportLogSchema.setCreateTime(new Date());
        exportLogSchema.setStatus("b");
        //设置元数据才能保存
        exportLogSchema.setMetadata(new Metadata());
        exportLogSchema.getMetadata().setName(exportLogSchema.getName());
        client.create(exportLogSchema).subscribe();

        //分页导出数据
        res.publishOn(Schedulers.boundedElastic()).
                doOnSuccess(listResult -> {
                    System.out.println("开始写文件123");
                    //循环获取数据并写入文件
                    //写文件
                    for (int i = 1; i <= listResult.getTotalPages(); i++) {
                        Mono<ListResult<Post>> listResult1 = client.list(Post.class, paramPredicate, null, i, pageSize);
                        listResult1.doOnNext(posts -> {
                            wirteMarkdown(posts.getItems(), exportLogSchema.getName());
                        }).subscribe();
                    }
                    //打包文件
                    File absoluteFile = getDocFile().toFile().getAbsoluteFile();
                    ZipUtil.zip(absoluteFile + "/" + exportLogSchema.getName(), absoluteFile + "/" + exportLogSchema.getName() + ".zip");

                    //修改状态
                    client.fetch(ExportLogSchema.class, exportLogSchema.getMetadata().getName()).doOnSuccess(
                            exportLog -> {
                                long now = System.currentTimeMillis();
                                exportLog.setCostSeconds(now - old);
                                exportLog.setStatus("c");
                                client.update(exportLog);
                            }).subscribe();
                }).subscribe();

        return res;
    }

    /**
     * 写文件
     *
     * @param items
     */
    private void wirteMarkdown(List<Post> items, String name) {
        for (Post post : items) {
            //获取文章详情
            client.fetch(Post.class, post.getMetadata().getName())
                    .publishOn(Schedulers.boundedElastic()) //切换到阻塞上下文
                    .doOnSuccess(wholePost -> {
                        //获取文章内容
                        String releaseSnapshot = post.getSpec().getReleaseSnapshot();
                        getContent(releaseSnapshot, post.getSpec().getBaseSnapshot())
                                .doOnSuccess(content -> {
                                    writeContent(post, content, name);
                                }).subscribe();
                    }).subscribe();


        }

    }

    /**
     * 写入文件内容
     *
     * @param post
     * @param content
     * @param name
     */
    private void writeContent(Post post, ContentWrapper content, String name) {
        Path docFile = getDocFile();
        File dir = new File(docFile.toFile().getAbsoluteFile() + "/" + name);
        //判读文件夹是否存在
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //markdown | html | json | asciidoc | latex
        String extendName = extendNameMap.get(StringUtils.lowerCase(content.getRawType()));

        //判断文件名是否合法
        String mdFileName = dir.getAbsoluteFile() + "/" + post.getSpec().getTitle() + extendName;
        if (!FileUtil.isFileNameValid(mdFileName)) {
            mdFileName = dir.getAbsolutePath() + "/" + post.getSpec().getSlug() + extendName;
        }

        try {
            File file = new File(mdFileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            //markdown文件的属性
            if (StringUtils.equals(extendName, ".md")) {
                Post.PostStatus postStatus = post.getStatus();
                Post.PostSpec postSpec = post.getSpec();
                MetadataOperator postMetadata = post.getMetadata();
                //  ---
                //  title: 快速开始
                //  date: 2023-10-22 16:56:20
                //  permalink: /pages/quickStart/
                //  categories:
                //  - cate1
                //  tags:
                //  - tag1
                //  ---
                writer.write("---\n");
                writer.write(String.format("title: %s\n", postSpec.getTitle()));

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formatDate = postMetadata.getCreationTimestamp().atZone(ZoneId.systemDefault()).format(formatter);
                writer.write(String.format("date: %s\n", formatDate));

                writer.write(String.format("auther: %s", postSpec.getOwner()));
                //摘录
                writer.write(String.format("excerpt: %s", postStatus.getExcerpt()));
                //永久链接
                writer.write(String.format("permalink: %s\n", postStatus.getPermalink()));
                //分类
                //分类-1 获取全部分类

                writer.write("categories:");
                if (postSpec.getCategories() != null) {
                    for (String category : postSpec.getCategories()) {
                        writer.write(String.format("\t-%s\n", category));
                    }
                }
                //标签
                writer.write("tags: \n");
                if (postSpec.getTags() != null) {
                    for (String tag : postSpec.getTags()) {
                        writer.write(String.format("\t-%s\n", tag));
                    }
                }
                writer.write("---\n");
            }

            //内容
            writer.write(content.getRaw());
            writer.flush();
            log.warn("写文件:{}-{}", content.getRawType(), file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    boolean contains(Collection<String> left, List<String> right) {
        // parameter is null, it means that ignore this condition
        if (left == null) {
            return true;
        }
        // else, it means that right is empty
        if (left.isEmpty()) {
            return right.isEmpty();
        }
        if (right == null) {
            return false;
        }
        return right.stream().anyMatch(left::contains);
    }

    private Set<String> listToSet(List<String> param) {
        return param == null ? null : Set.copyOf(param);
    }


    private Path getDocFile() {
        String userHome = System.getProperty("user.home");
        Path path = Paths.get(userHome, ".halo").resolve("plugins").resolve("export2doc_files");
        if (!path.toFile().exists()) {
            path.toFile().mkdirs();
        }
        return path;
    }


    /**
     * 获取文章详情
     *
     * @param snapshotName
     * @param baseSnapshotName
     * @return
     */
    public Mono<ContentWrapper> getContent(String snapshotName, String baseSnapshotName) {
        if (StringUtils.isBlank(snapshotName) || StringUtils.isBlank(baseSnapshotName)) {
            return Mono.empty();
        }
        // TODO: refactor this method to use client.get instead of fetch but please be careful
        return client.fetch(Snapshot.class, baseSnapshotName)
                .doOnNext(this::checkBaseSnapshot)
                .flatMap(baseSnapshot -> {
                    if (StringUtils.equals(snapshotName, baseSnapshotName)) {
                        var contentWrapper = ContentWrapper.patchSnapshot(baseSnapshot, baseSnapshot);
                        return Mono.just(contentWrapper);
                    }
                    return client.fetch(Snapshot.class, snapshotName)
                            .map(snapshot -> ContentWrapper.patchSnapshot(snapshot, baseSnapshot));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("The content snapshot [{}] or base snapshot [{}] not found.",
                            snapshotName, baseSnapshotName);
                    return Mono.empty();
                }));
    }

    protected void checkBaseSnapshot(Snapshot snapshot) {
        Assert.notNull(snapshot, "The snapshot must not be null.");
        String keepRawAnno =
                MetadataUtil.nullSafeAnnotations(snapshot).get(Snapshot.KEEP_RAW_ANNO);
        if (!StringUtils.equals(Boolean.TRUE.toString(), keepRawAnno)) {
            throw new IllegalArgumentException(
                    String.format("The snapshot [%s] is not a base snapshot.",
                            snapshot.getMetadata().getName()));
        }
    }


    public static String replacePermalink(String pattern, String permalink) {

    }
}
