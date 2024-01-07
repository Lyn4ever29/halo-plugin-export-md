<script lang="ts" setup>
import axios from "axios";
import {
  Dialog,
  Toast,
  VButton,
  VEmpty,
  VLoading,
  VSpace,
} from "@halo-dev/components";
import ExportItem from "@/views/ExportListItem.vue";
import { useQuery } from "@tanstack/vue-query";
import type { ListedExportLogList } from "../domain";

const http = axios.create({
  baseURL: "/",
  timeout: 1000,
});

const nowTime = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, "0");
  const day = String(now.getDate()).padStart(2, "0");
  const hours = String(now.getHours()).padStart(2, "0");
  const minutes = String(now.getMinutes()).padStart(2, "0");
  const seconds = String(now.getSeconds()).padStart(2, "0");

  return year + month + day + hours + minutes + seconds;
};

const { data, isLoading, isFetching, refetch } = useQuery({
  queryKey: ["plugin:export2doc:exportLogs"],
  queryFn: async () => {
    const { data } = await http.get<ListedExportLogList>(
      "/apis/cn.lyn4ever.export2doc/v1alpha1/exportLogs"
    );
    return data;
  },
  refetchInterval(data) {
    const pendingData = data?.items.some(
      (item) => !!item.metadata.deletionTimestamp || item.status === "b"
    );

    return pendingData ? 1000 : false;
  },
});

const confirmExport = () => {
  Dialog.warning({
    title: "新增导出",
    description:
      "点击按钮后，后台将进行导出，请耐心等待完成，导出完成后，点击名称即可下载。",
    confirmType: "primary",
    confirmText: "确定",
    cancelText: "取消",
    onConfirm: async () => {
      try {
        await http
          .post(
            "/apis/api.plugin.halo.run/v1alpha1/plugins/export2doc/doExport/export",
            {
              name: "export2doc_" + nowTime(),
              createTime: "",
              costSeconds: "",
              tag: "",
              category: "",
              beginTime: "",
              endTime: "",
              remainMetaData: true,
              remainCategory: true,
            }
          )
          .then(() => {
            Toast.success("新增导出成功");
            refetch();
          });
      } catch (e) {
        console.error("Failed to submit exportLog", e);
      }
    },
  });
};

// 输出组件的方法，让外部组件可以调用
defineExpose({
  confirmExport,
});
</script>

<template>
  <VLoading v-if="isLoading" />

  <Transition v-else-if="!data?.items.length" appear name="fade">
    <VEmpty
      message="当前还没有导出记录，你可以尝试导出数据或者刷新。"
      title="暂无导出记录"
    >
      <template #actions>
        <VSpace>
          <VButton :loading="isFetching" @click="refetch()"> 刷新 </VButton>
          <VButton type="secondary" @click="confirmExport"> 导出 </VButton>
        </VSpace>
      </template>
    </VEmpty>
  </Transition>

  <Transition v-else appear name="fade">
    <ul class="box-border h-full w-full divide-y divide-gray-100" role="list">
      <li v-for="item in data.items" :key="item.metadata.name">
        <ExportItem :export-log="item" />
      </li>
    </ul>
  </Transition>
</template>
