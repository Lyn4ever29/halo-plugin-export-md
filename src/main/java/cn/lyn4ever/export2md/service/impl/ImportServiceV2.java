package cn.lyn4ever.export2md.service.impl;

import cn.hutool.core.lang.UUID;
import cn.lyn4ever.export2md.halo.PostRequest;
import cn.lyn4ever.export2md.schema.ImportLogSchema;
import cn.lyn4ever.export2md.service.ImportService;
import cn.lyn4ever.export2md.service.PostService;
import java.io.File;
import java.security.Principal;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import run.halo.app.core.extension.content.Post;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.ReactiveExtensionClient;

/**
 * @author Lyn4ever29
 * @url https://jhacker.cn
 * @date 2023/12/5
 */
@Service
@RequiredArgsConstructor
public class ImportServiceV2 implements ImportService {
    private final PostService postService;
    private final ReactiveExtensionClient client;

    /**
     * 运行导出任务
     *
     * @param file
     * @return
     */
    @Override
    public Mono<Post> runTask(File file) {
        long old = System.currentTimeMillis();
        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .map(Principal::getName)
            .flatMap(owner -> {
                PostRequest postRequest = postService.formatPost(file);
                Post post = postRequest.getPost();
                // post.getStatus().setContributors(List.of(owner));

                // https://jhacker.cn/apis/api.console.halo.run/v1alpha1/posts
                return postService.draftPost(postRequest).doOnSuccess(re->{

                    ImportLogSchema schema = new ImportLogSchema();
                    schema.setCreateTime(new Date());
                    schema.setPostName(post.getMetadata().getName());
                    schema.setTitle(post.getSpec().getTitle());
                    schema.setCostSeconds(System.currentTimeMillis() - old);
                    schema.setMetadata(new Metadata());
                    schema.getMetadata().setName(UUID.fastUUID().toString(false));

                    client.create(schema);
                });

            });



        /*
        {
          "post":{
            "spec":{
              "title":"111",
              "slug":"111",
              "template":"",
              "cover":"",
              "deleted":false,
              "publish":false,
              "pinned":false,
              "allowComment":true,
              "visible":"PUBLIC",
              "priority":0,
              "excerpt":{
                "autoGenerate":true,
                "raw":""
              },
              "categories":[
              ],
              "tags":[
              ],
              "htmlMetas":[
              ]
            },
            "apiVersion":"content.halo.run/v1alpha1",
            "kind":"Post",
            "metadata":{
              "name":"3ad608e7-a0cb-482b-9eff-fc5883a3575b",
              "annotations":{
                "content.halo.run/preferred-editor":"bytemd"
              }
            }
          },
          "content":{
            "raw":" ## title\n name\n - 都\n - [x] 哈哈\n - [ ] haode ",
            "content":"<h2 id=\"title\">title</h2>\n<p>name</p>\n<ul
            class=\"contains-task-list\">\n<li>都</li>\n<li class=\"task-list-item\"><input
            type=\"checkbox\" checked disabled> 哈哈</li>\n<li class=\"task-list-item\"><input
            type=\"checkbox\" disabled> haode</li>\n</ul>",
            "rawType":"markdown"
          }
        }
         */

    }
}
