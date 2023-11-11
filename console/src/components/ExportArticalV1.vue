<script lang="ts" setup>
import {onMounted, reactive, ref} from 'vue'
import axios from "axios";
import {buildCategoriesTree} from "../utils";


const dialogTableVisible = ref(false)
const formLabelWidth = '140px'


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
  name: "导出文章_" + nowTime(),
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
const onSubmit = () => {
  http
      .post("/apis/api.plugin.halo.run/v1alpha1/plugins/export2doc/doExport/export", form)
      .then((res: any) => {
        console.log(res);
        dialogTableVisible.value = false
      });


}
 
const init = () => {
  http.get("/apis/cn.lyn4ever.export2doc/v1alpha1/exportLogs")
      .then(res => {
        exportLogData.value=res.data.items
      })
}

//初始化
onMounted(init);



</script>

<template>

  <el-button type="primary" @click="dialogTableVisible = true">
    导出
  </el-button>

  <el-table :data="exportLogData">
    <el-table-column type="index" width="50"/>

    <el-table-column align="center" property="name" label="名称">
      <template #default="scope">
        <el-link type="primary">${scope.name}</el-link>
      </template>
    </el-table-column>
    <el-table-column align="center" property="createTime" label="创建时间"/>
    <el-table-column align="center" property="costSeconds" label="花费时间（秒）"/>
  </el-table>

  <!--  新增导出-->
  <el-dialog v-model="dialogTableVisible" title="新建导出">
    <ul>
      <li>点击按钮后，后台将进行导出，请耐心等待完成；</li>
      <li>导出完成后，点击名称即可下载</li>
    </ul>


    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogTableVisible = false">取消</el-button>
        <el-button type="primary" @click="onSubmit">
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">

.el-button {
  background-color: var(--el-button-bg-color) !important;
}

</style>
