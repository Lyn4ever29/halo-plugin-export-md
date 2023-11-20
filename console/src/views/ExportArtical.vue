<script lang="ts" setup>
import {onMounted, reactive, ref} from 'vue'
import axios from "axios";
import {Dialog, Toast} from "@halo-dev/components";
import ExportItem from "@/views/ExportListItem.vue";


const http = axios.create({
  baseURL: "/",
  timeout: 1000,
});

const nowTime = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  const hours = String(now.getHours()).padStart(2, '0');
  const minutes = String(now.getMinutes()).padStart(2, '0');
  const seconds = String(now.getSeconds()).padStart(2, '0');

  return year + month + day + hours + minutes + seconds;
}


// do not use same name with ref
const form = reactive({
  name: "export2doc_" + nowTime(),
  createTime: "",
  costSeconds: "",
  tag: "",
  category: "",
  beginTime: "",
  endTime: "",
  remainMetaData: true,
  remainCategory: true
})

const exportLogData = ref(
    []
)

const init = () => {
  http.get("/apis/cn.lyn4ever.export2doc/v1alpha1/exportLogs")
      .then(res => {
        exportLogData.value = res.data.items
      })
}

//初始化
onMounted(init);


const confirmExport = () => {
  Dialog.warning({
    title: "新增导出",
    description: "点击按钮后，后台将进行导出，请耐心等待完成，导出完成后，点击名称即可下载。",
    confirmType: "danger",
    confirmText: "确定",
    cancelText: "取消",
    onConfirm: async () => {
      try {
        await http.post("/apis/api.plugin.halo.run/v1alpha1/plugins/export2doc/doExport/export", form)
            .then((res: any) => {
              Toast.success("新增导出成功");
              setTimeout(() => init(), 2000)
            });
      } catch (e) {
        console.error("Failed to submit exportLog", e);
      }
    },
  });
}


// 输出组件的方法，让外部组件可以调用
defineExpose({
  confirmExport
})

</script>

<template>
  <div>
    <ul class="box-border h-full w-full divide-y divide-gray-100" role="list">
      <li v-for="(item, index) in exportLogData" :key="index">
        <ExportItem @update="init" :exportLog="item"/>
      </li>
    </ul>
  </div>

</template>

<style scoped lang="scss">

</style>
