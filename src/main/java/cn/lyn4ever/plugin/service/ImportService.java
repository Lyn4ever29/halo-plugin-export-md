package cn.lyn4ever.plugin.service;

import cn.hutool.core.lang.UUID;
import cn.lyn4ever.plugin.schema.ImportLogSchema;
import cn.lyn4ever.plugin.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.markdown4j.Markdown4jProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import run.halo.app.core.extension.content.Post;
import run.halo.app.core.extension.content.Snapshot;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.MetadataUtil;
import run.halo.app.extension.Ref;

import java.io.*;
import java.util.*;

/**
 * @author Lyn4ever29
 * @url https://jhacker.cn
 * @date 2023/11/12
 */
@Component
@Slf4j
public class ImportService {


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
     * @param filePart
     */
    public void runTask(FilePart filePart,String owner) {
        long old = System.currentTimeMillis();

        //保存文件
        File file = new File(FileUtil.getDocFile(FileUtil.DirPath.IMPORT).toFile().getAbsolutePath()
                + "/" + filePart.filename());
        filePart.transferTo(file).block();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        //todo 先读取第一行，判断是否包含属性

        StringJoiner sj = new StringJoiner("\n");
        reader.lines().forEach(sj::add);

//        //读取第一行，判断是否包含属性
//        String line = reader.readLine();
//        if (StringUtils.equals("---", line)) {
//            //读取属性
//            while (StringUtils.equals("---", line)) {
//
//            }
//        }

        String title = file.getName().split(".md")[0];

        Post post = new Post();

        Post.PostSpec postSpec = new Post.PostSpec();
        postSpec.setTitle(title);
        postSpec.setSlug(UUID.fastUUID().toString(false));
        postSpec.setAllowComment(true);
        postSpec.setDeleted(false);
        Post.Excerpt excerpt = new Post.Excerpt();
        excerpt.setAutoGenerate(true);
        postSpec.setExcerpt(excerpt);
        postSpec.setPriority(0);
        postSpec.setVisible(Post.VisibleEnum.PUBLIC);
        postSpec.setPublish(false);
        postSpec.setPinned(false);


        Post.PostStatus postStatus = new Post.PostStatus();
        //草稿箱，待发布状态
        postStatus.setPhase(Post.PostPhase.DRAFT.name());
        postStatus.setContributors(List.of(owner));

        post.setSpec(postSpec);
        post.setStatus(postStatus);
        //设置元数据才能保存
        post.setMetadata(new Metadata());
        post.getMetadata().setName(UUID.fastUUID().toString(false));


        Snapshot.SnapShotSpec snapShotSpec = new Snapshot.SnapShotSpec();
        snapShotSpec.setRawType("markdown");
        snapShotSpec.setRawPatch(sj.toString());
        try {
            snapShotSpec.setContentPatch(new Markdown4jProcessor().process(sj.toString()));
        } catch (IOException e) {
            snapShotSpec.setContentPatch(snapShotSpec.getRawPatch());
        }

        snapShotSpec.setSubjectRef(Ref.of(post));
        snapShotSpec.setOwner(owner);

        Snapshot snapshot = new Snapshot();
        snapshot.setSpec(snapShotSpec);
        //设置元数据才能保存
        snapshot.setMetadata(new Metadata());
        snapshot.getMetadata().setName(UUID.fastUUID().toString(false));
        MetadataUtil.nullSafeAnnotations(snapshot).put(Snapshot.KEEP_RAW_ANNO, "true");

        client.create(snapshot);

        postSpec.setBaseSnapshot(snapshot.getMetadata().getName());
        postSpec.setHeadSnapshot(snapshot.getMetadata().getName());
        postSpec.setReleaseSnapshot(snapshot.getMetadata().getName());



        ImportLogSchema schema = new ImportLogSchema();
        schema.setCreateTime(new Date());
        schema.setPostName(post.getMetadata().getName());
        schema.setTitle(title);
        schema.setCostSeconds(System.currentTimeMillis() - old);
        //设置元数据才能保存
        schema.setMetadata(new Metadata());
        schema.getMetadata().setName(UUID.fastUUID().toString(false));

        client.create(post);
        client.create(schema);
    }

}



