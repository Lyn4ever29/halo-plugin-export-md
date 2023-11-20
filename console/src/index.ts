import {definePlugin} from "@halo-dev/console-shared";
import HomeView from "./views/HomeView.vue";
import {IconArrowUpDownLine} from "@halo-dev/components";
import {markRaw} from "vue";

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
  extensionPoints: {},
});

