import {definePlugin} from "@halo-dev/console-shared";
import HomeView from "./views/HomeView.vue";
import {IconArrowUpDownLine, VDropdownItem} from "@halo-dev/components";
import {markRaw} from "vue";
import type {ListedPost} from "@halo-dev/api-client";
import axios from "axios";

// @ts-ignore
export default definePlugin({
    components: {},
    routes: [
        {
            parentName: "Root",
            route: {
                path: "/export2doc",
                name: "export2doc",
                component: HomeView,
                meta: {
                    title: "文章导入导出",
                    searchable: true,
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
        "post:list-item:operation:create": () => {
            return [
                {
                    priority: 21,
                    component: markRaw(VDropdownItem),
                    label: "导出",
                    permissions: [],
                    action: async (post: ListedPost) => {
                        window.location.href = '/apis/api.plugin.halo.run/v1alpha1/plugins/export2doc/doExport/export_one/' + post.post.metadata.name
                    },
                },
            ];
        }
    }
});

