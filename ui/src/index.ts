import {definePlugin, type OperationItem} from "@halo-dev/console-shared";
import HomeView from "./views/HomeView.vue";
import {IconArrowUpDownLine, VDropdownItem} from "@halo-dev/components";
import {markRaw, type Ref} from "vue";
import type {ListedPost} from "@halo-dev/api-client";

// @ts-ignore
export default definePlugin({
    components: {},
    routes: [
        {
            parentName: "ToolsRoot",
            route: {
                path: "export2doc",
                name: "export2doc",
                component: HomeView,
                meta: {
                    title: "文章导入导出",
                    searchable: true,
                    description: "导出文章为 Markdown、HTML 文件，同时支持导入 Markdown 文件",
                    permissions: ["plugin:export2doc:view"],
                    menu: {
                        name: "文章导入导出",
                        group: "tool",
                        icon: markRaw(IconArrowUpDownLine),
                        priority: 0,
                    },
                },
            },
        },
    ],
    extensionPoints: {
        "post:list-item:operation:create": ():OperationItem<ListedPost>[] => {
            return [
                {
                    priority: 21,
                    component: markRaw(VDropdownItem),
                    label: "导出",
                    permissions: [],
                    action: (item: ListedPost | undefined):void => {
                      if (item != undefined) {
                        window.location.href = '/apis/api.plugin.halo.run/v1alpha1/plugins/export2doc/doExport/export_one/' + item.post.metadata.name;
                      }
                    },
                    hidden: false,
                    children: [],
                },
            ];
        }
    }
});
