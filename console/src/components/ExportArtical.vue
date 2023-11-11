<script lang="ts" setup>
import {onMounted, reactive, ref} from 'vue'
import axios from "axios";
import {buildCategoriesTree} from "../utils";

const nowTime = new Date().toLocaleString('en-US', {year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', second: '2-digit'}).replace(/[^\\d]/g, '');


const dialogTableVisible = ref(false)
const dialogFormVisible = ref(false)
const formLabelWidth = '140px'

const http = axios.create({
  baseURL: "/",
  timeout: 1000,
});


// do not use same name with ref
const form = reactive({
  name: "导出文章_"+nowTime,
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


// {
//   name:"",
//     tag:"",
//   category:"",
//   beginTime:"",
//   endTime:"",
//   remainMetaData:"",
//   remainCategory:"",
//   createTime:"",
//   costSeconds:""
// }
const onSubmit = () => {
  console.log('submit!')
  http
      .post("/apis/api.plugin.halo.run/v1alpha1/plugins/export2doc/doExport/export", form)
      .then((res: any) => {
        console.log(res);
      });


}

let tagList = ref({
  first: false,
  hasNext: false,
  hasPrevious: false,
  items: [],
  last: false,
  page: 0,
  size: 0,
  total: 0,
  totalPages: 0
})
let categoryList = ref({
  first: false,
  hasNext: false,
  hasPrevious: false,
  items: [],
  last: false,
  page: 0,
  size: 0,
  total: 0,
  totalPages: 0
})
const treeProps = {
  label: "spec.displayName",
  children: "spec.children"
}
const init = () => {
  http.get("/apis/cn.lyn4ever.export2doc/v1alpha1/exportLogs")
      .then(res => {
        console.log(res)
      })

  http.get('/apis/content.halo.run/v1alpha1/tags?page=0&size=0')
      .then(res => {
        tagList = ref(res.data)
      })
  http.get('/apis/content.halo.run/v1alpha1/categories?page=0&size=0')
      .then(res => {

        let trees = buildCategoriesTree(res.data.items)

        //重新格式化
        formatTree(trees)
        console.log(trees);
        categoryList = ref(trees)

      })
}
const formatTree = function (tree) {
  if (tree == null) {
    alert(1111)
  }
  console.log(1, tree);
  for (let i = 0; i < tree.length; i++) {
    let item = tree[i]
    tree[i] = {
      ...tree[i],
      value: item.metadata.name,
      label: item.spec.displayName,
      children: (item.spec.children != null && item.spec.children.length > 0) ? formatTree(item.spec.children) : []
    }
  }
  return tree
};

//初始化
onMounted(init);

let selectCate = ref([])
const selectCategory = function (category) {
  form.category = ref(
      selectCate.value.toString()
  )
  console.log(form.category);
}



</script>

<template>

  <el-button type="primary" @click="dialogTableVisible = true">
    新增导出
  </el-button>

  <el-table :data="exportLogData">
    <el-table-column type="index" width="50"/>

    <el-table-column align="center" label="导出条件">
      <el-table-column align="center" property="name" label="名称"/>
      <el-table-column align="center" property="tag" label="标签"/>
      <el-table-column align="center" property="category" label="分类"/>
      <el-table-column align="center" property="beginTime" label="开始时间"/>
      <el-table-column align="center" property="endTime" label="结束时间"/>
      <el-table-column align="center" property="remainMetaData" label="导出元数据"/>
      <el-table-column align="center" property="remainCategory" label="保留分类目录"/>
    </el-table-column>
    <el-table-column align="center" label="导出结果">
      <el-table-column align="center" property="createTime" label="创建时间"/>
      <el-table-column align="center" property="costSeconds" label="花费时间（秒）"/>
      <el-table-column align="center" property="download" label="下载">
        <template #default="scope">
          <el-link type="primary">下载</el-link>
        </template>
      </el-table-column>
    </el-table-column>
  </el-table>

  <!--  新增导出-->
  <el-dialog v-model="dialogTableVisible" title="新建导出">
    <el-form :model="form" label-width="120px">
      <el-form-item label="项目名称" :placeholder="form.name">
        <el-input v-model="form.name"/>
      </el-form-item>
      <el-form-item label="时间范围">
        <el-col :span="11">
          <el-date-picker
              v-model="form.beginTime"
              type="date" format="YYYY/MM/DD"
              placeholder="开始时间"
              style="width: 100%"
          />
        </el-col>
        <el-col :span="2" class="text-center">
          <span class="text-gray-500">-</span>
        </el-col>
        <el-col :span="11">
          <el-date-picker
              v-model="form.endTime"
              type="date" format="YYYY/MM/DD"
              placeholder="结束时间"
              style="width: 100%"
          />
        </el-col>
      </el-form-item>
      <el-form-item label="标签">
        <el-select v-model="form.tag" class="m-2" placeholder="标签" size="large" filterable>
          <el-option
              v-for="item in tagList.items"
              :key="item.spec.displayName"
              :label="item.spec.displayName"
              :value="item.metadata.name"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="分类">
        <el-tree-select v-model="selectCate"
                        @change="selectCategory"
                        show-checkbox multiple
                        :data="categoryList" :render-after-expand="false"/>
      </el-form-item>
      <el-form-item label="附加属性">
        <el-checkbox v-model="form.remainMetaData" label="导出元数据"/>
        <el-checkbox v-model="form.remainCategory" label="保留分类目录"/>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取消</el-button>
        <el-button type="primary" @click="onSubmit">
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">

.el-button{
  background-color: var(--el-button-bg-color) !important;
}

</style>
