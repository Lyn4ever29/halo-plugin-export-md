package cn.lyn4ever.plugin.service;

import cn.hutool.core.util.ZipUtil;
import cn.lyn4ever.plugin.dto.ContentWrapper;
import cn.lyn4ever.plugin.schema.ExportLogSchema;
import cn.lyn4ever.plugin.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import run.halo.app.core.extension.content.Category;
import run.halo.app.core.extension.content.Post;
import run.halo.app.core.extension.content.Snapshot;
import run.halo.app.core.extension.content.Tag;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.ListResult;
import run.halo.app.extension.MetadataOperator;
import run.halo.app.extension.MetadataUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

/**
 * @author Lyn4ever29
 * @url https://jhacker.cn
 * @date 2023/11/12
 */
@Component
@Slf4j
public class ExportService {


    public static ThreadLocal<Map<String, String>> TagPool = new ThreadLocal<>();
    public static ThreadLocal<Map<String, String>> CatePool = new ThreadLocal<>();


    private final int pageSize = 20;

    private final Map<String, String> extendNameMap = new HashMap<>() {{
        put("markdown", ".md");
        put("html", ".html");
        put("json", ".josn");

    }};
    @Autowired
    private ExtensionClient client;

    /**
     * 运行导出任务
     *
     * @param exportLogSchema
     */
    public void runTask(ExportLogSchema exportLogSchema) {
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


        ListResult<Post> listResult = client.list(Post.class, paramPredicate, null, 1, pageSize);


        //分页导出数据
        //分页获取文章并处理
        for (int i = 1; i <= listResult.getTotalPages(); i++) {
            ListResult<Post> posts = client.list(Post.class, paramPredicate, null, i, pageSize);
            posts.getItems().forEach(post -> {
                writePost(post.getMetadata().getName(), exportLogSchema.getName());
            });
        }
        //打包文件
        File absoluteFile = FileUtil.getDocFile(FileUtil.DirPath.EXPORT).toFile().getAbsoluteFile();
        ZipUtil.zip(absoluteFile + "/" + exportLogSchema.getName(), absoluteFile + "/" + exportLogSchema.getName() + ".zip");

        //修改状态
        client.fetch(ExportLogSchema.class, exportLogSchema.getMetadata().getName())
                .ifPresent(exportLog -> {
                    long now = System.currentTimeMillis();
                    exportLog.setCostSeconds(now - old);
                    exportLog.setStatus("c");
                    client.update(exportLog);
                });
    }

    /**
     * 写文件
     *
     * @param items
     * @param dirName 导出计划名称-保存文件夹
     */
//    private void detailPost(List<Post> items, String dirName) {
//        for (Post post : items) {
//            //获取文章详情
//            client.fetch(Post.class, post.getMetadata().getName())
//                    .ifPresent(wholePost -> {
//                        //获取文章内容
//                        String releaseSnapshot = wholePost.getSpec().getReleaseSnapshot();
//                        ContentWrapper content = getContent(releaseSnapshot, wholePost.getSpec().getBaseSnapshot());
//                        if (null != content) {
//                            writeContent(post, content, dirName);
//                        }
//                    });
//        }
//    }

    /**
     * 将文章到文件里
     *
     * @param name 文章的name
     * @param path 保存的文件夹(上一级)
     */
    public String writePost(String name, String path) {
        Optional<Post> fetch = client.fetch(Post.class, name);
        if (fetch.isPresent()) {
            Post wholePost = fetch.get();
            //获取文章内容
            String releaseSnapshot = wholePost.getSpec().getReleaseSnapshot();
            ContentWrapper content = getContent(releaseSnapshot, wholePost.getSpec().getBaseSnapshot());
            if (null != content) {
                return writeContent(wholePost, content, path);
            }
        }
        return "";
    }

    /**
     * 写入文件内容
     *
     * @param post
     * @param content
     * @param name
     */
    private String writeContent(Post post, ContentWrapper content, String name) {
        Path docFile = FileUtil.getDocFile(FileUtil.DirPath.EXPORT);
        File dir = new File(docFile.toFile().getAbsoluteFile() + "/" + name);
        //判读文件夹是否存在
        if (!dir.exists()) {
            dir.mkdirs();
        }

        //markdown | html | json | asciidoc | latex
        String extendName = extendNameMap.get(StringUtils.lowerCase(content.getRawType()));

        //判断文件名是否合法
        String fileName = post.getSpec().getTitle();
        if (!FileUtil.isCorrectName(fileName)) {
            fileName = post.getSpec().getSlug();
        }
        String finalFileName = dir.getAbsoluteFile() + "/" + fileName + extendName;


        try {
            File file = new File(finalFileName);
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

                writer.write(String.format("auther: %s\n", postSpec.getOwner()));
                //摘录
                writer.write(String.format("excerpt: %s\n", postStatus.getExcerpt().replaceAll("\n", "")));
                //永久链接
                writer.write(String.format("permalink: %s\n", postStatus.getPermalink()));
                //分类
                //分类-1 获取全部分类

                writer.write("categories:\n");
                if (postSpec.getCategories() != null) {
                    for (String category : postSpec.getCategories()) {
                        writer.write(String.format("\t-%s\n", getCateTrueName(category)));
                    }
                }
                //标签
                writer.write("tags: \n");
                if (postSpec.getTags() != null) {
                    for (String tag : postSpec.getTags()) {
                        writer.write(String.format("\t-%s\n", getTagTrueName(tag)));
                    }
                }
                writer.write("---\n\n");
            }

            //内容
            writer.write(content.getRaw());
            writer.flush();
            log.warn("写文件:{}-{}", content.getRawType(), file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return finalFileName;
    }

    /**
     * 根据id来查询真是的分类名称
     *
     * @param name
     * @return
     */
    private String getCateTrueName(String name) {
        Map<String, String> map = CatePool.get();
        if (null == map) {
            map = new HashMap<>();
        }
        String cateName = map.get(name);

        if (StringUtils.isNotBlank(cateName)) {
            return cateName;
        } else {
            //查询
            Optional<Category> fetch = client.fetch(Category.class, name);
            if (fetch.isPresent()) {
                Category category = fetch.get();
                map.put(name, category.getSpec().getSlug());
                return category.getSpec().getSlug();
            } else {
                //没有找到合适的
                return name;
            }
        }
    }

    /**
     * 根据id查找tag
     *
     * @param name
     * @return
     */
    private String getTagTrueName(String name) {
        Map<String, String> map = TagPool.get();
        if (null == map) {
            map = new HashMap<>();
        }
        String tagName = map.get(name);
        if (StringUtils.isNotBlank(tagName)) {
            return tagName;
        } else {
            //查询
            Optional<Tag> fetch = client.fetch(Tag.class, name);
            if (fetch.isPresent()) {
                Tag tag = fetch.get();
                map.put(name, tag.getSpec().getSlug());
                return tag.getSpec().getSlug();
            } else {
                return name;
            }
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


    /**
     * 获取文章详情
     *
     * @param snapshotName
     * @param baseSnapshotName
     * @return
     */
    public ContentWrapper getContent(String snapshotName, String baseSnapshotName) {
        if (StringUtils.isBlank(snapshotName) || StringUtils.isBlank(baseSnapshotName)) {
            return null;
        }
        AtomicReference<ContentWrapper> wrapper = new AtomicReference<>(null);
        // TODO: refactor this method to use client.get instead of fetch but please be careful
        client.fetch(Snapshot.class, baseSnapshotName)
                .ifPresent(baseSnapshot -> {
                    checkBaseSnapshot(baseSnapshot);
                    if (StringUtils.equals(snapshotName, baseSnapshotName)) {
                        wrapper.set(ContentWrapper.patchSnapshot(baseSnapshot, baseSnapshot));
                    }
                    client.fetch(Snapshot.class, snapshotName)
                            .ifPresent(snapshot ->
                                    wrapper.set(ContentWrapper.patchSnapshot(snapshot, baseSnapshot)));
                });

        return wrapper.get();
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

    public void delete(String name) {

        String fileName = FileUtil.getDocFile(FileUtil.DirPath.EXPORT).toFile().getAbsolutePath() + "/" + name;

        //删除文件夹
        cn.hutool.core.io.FileUtil.del(fileName);
        //删除压缩文件
        cn.hutool.core.io.FileUtil.del(fileName + ".zip");


    }
}



