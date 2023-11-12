<script lang="ts" setup>
import {onMounted, reactive, ref} from 'vue'
import axios from "axios";
import {Dialog, IconAddCircle, Toast, VButton, VTag,IconDeleteBin} from "@halo-dev/components";
// const { t } = useI18n();

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


const statusList = ref({
  "a": "失败",
  "b": "导出中",
  "c": "成功",
})
const statusTag = ref({
  "a": "danger",
  "b": "secondary",
  "c": "primary",
  "d": "default"
})
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
        init()
      });


}

const init = () => {
  http.get("/apis/cn.lyn4ever.export2doc/v1alpha1/exportLogs")
      .then(res => {
        exportLogData.value = res.data.items
      })
}

//初始化
onMounted(init);


const selectItem = ref('')
const tableSelect = (selection: any) => {
  console.log(selection)
  let res = selection.map((obj: { name: any; })=>{
    return obj.name
  })
  selectItem.value = res
}


const clickDel = () => {
  Dialog.warning({
    title: "确定删除吗？",
    description: "删除导出记录后，已经导出的文件也会被删除，此操作不可逆，确定吗？",
    confirmType: 'danger',
    confirmText: "确定",
    cancelText: "取消",
    async onConfirm() {
      http.post("/apis/api.plugin.halo.run/v1alpha1/plugins/export2doc/doExport/del", 
          selectItem.value)
          .then((res: any) => {
            Toast.success("删除成功");
            init()
          })
          .catch(() => {
            Toast.error("删除失败，请稍后再试");
          })
    },
  });


}


const formatDate = (s: string | number | Date) => {
  return new Date(s).toLocaleString();
}
</script>

<template>
  <div style="padding: 20px">
    <VButton type="primary" @click="dialogTableVisible = true">
      <template #icon>
        <IconAddCircle class="h-full w-full"/>
      </template>
      导出
    </VButton>
    <VButton  v-if="selectItem.length>0" type="danger" @click="clickDel" style="margin-left:10px">
      <template #icon>
        <IconDeleteBin class="h-full w-full"/>
      </template>
      删除
    </VButton>
    <!--  <el-button v-if="selectItem.length>0" type="danger" @click="clickDel">-->
    <!--    删除-->
    <!--  </el-button>-->


    <el-table :data="exportLogData" @selection-change="tableSelect">
          <el-table-column type="selection" width="55"/>
      <el-table-column align="center" type="index" label="序号" width="80"/>
      <el-table-column align="center" property="name" label="名称" width="250" >
        <template #default="scope">
          <el-link type="primary" target='_blank' v-if="scope.row.status=='c'"
                   :href="'/apis/api.plugin.halo.run/v1alpha1/plugins/export2doc/doExport/down/'+scope.row.name"
          >{{ scope.row.name }}
          </el-link>
          <span v-else>{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" property="createTime" label="创建时间">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column align="center" property="costSeconds" label="耗时（秒）">
        <template #default="scope">
          {{ Math.round(scope.row.costSeconds / 1000) }}
        </template>
      </el-table-column>
      <el-table-column align="center" property="status" label="状态">
        <template #default="scope">
          <VTag :theme="statusTag[scope.row.status]">{{ statusList[scope.row.status] }}</VTag>
        </template>
      </el-table-column>

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
  </div>

</template>

<style scoped lang="scss">

.el-button {
  background-color: var(--el-button-bg-color) !important;
}

</style>
