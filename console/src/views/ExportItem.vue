<script lang="ts" setup>
import {markRaw, onMounted, reactive, ref, toRefs} from 'vue'
import axios from "axios";
import {Dialog, Toast, VDropdownItem, VEntity, VEntityField, VSpace, VStatusDot} from "@halo-dev/components";
// const { t } = useI18n();
import type {ExportLog} from '@/domain'
import EntityDropdownItems from '../components/entity/EntityDropdownItems.vue'

const props = withDefaults(
  defineProps<{
    exportLog: ExportLog;
    showOperations: boolean;
  }>(),
  {
    showOperations: true,
  }
);


const {exportLog} = toRefs(props);


const operationItems = ref(
  [
    {
      priority: 10,
      component: markRaw(VDropdownItem),
      label: "下载",
      hidden: props.exportLog.status !== "c",
      permissions: [],
      action: () => {
        window.location.href = '/apis/api.plugin.halo.run/v1alpha1/plugins/export2doc/doExport/down/' + props.exportLog.name
      },
    },
    {
      priority: 20,
      component: markRaw(VDropdownItem),
      props: {
        type: "danger",
      },
      label: "删除",
      action: () => {
        selectItem.value =[ props.exportLog.name]
        clickDel()
      },
    },
  ]
);


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
  "a": "error",
  "b": "warning",
  "c": "success",
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


}

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
            setTimeout(() => init(), 1000)
          });
      } catch (e) {
        console.error("Failed to submit exportLog", e);
      }
    },
  });
}

const selectItem = ref('')
const tableSelect = (selection: any) => {
  console.log(selection)
  selectItem.value = selection.map((obj: { name: any; }) => {
    return obj.name
  })
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
          setTimeout(() => init(), 1000)
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

  <VEntity>
    <template #start>
      <VEntityField
        :description="exportLog.name"
      >
        <template #description>
          <VSpace class="flex-wrap">
                  <span class="text-xs text-gray-500">
                    {{ exportLog.name }}
                  </span>
            <!--                  <span class="text-xs text-gray-500">-->
            <!--                    {{ prettyBytes(backup.status?.size || 0) }}-->
            <!--                  </span>-->
          </VSpace>
        </template>
      </VEntityField>
    </template>
    <template #end>
      <VEntityField>
        <template #description>
          <VStatusDot
            :state="statusTag[exportLog.status]"
            :text="statusList[exportLog.status]"
          />
        </template>
      </VEntityField>
      <VEntityField>
        <template #description>
                <span class="truncate text-xs tabular-nums text-gray-500">
                 {{ exportLog.costSeconds }}ms
                </span>
        </template>
      </VEntityField>
      <VEntityField>
        <template #description>
                <span class="truncate text-xs tabular-nums text-gray-500">
                   {{ formatDate(exportLog.createTime) }}
                </span>
        </template>
      </VEntityField>
      <slot name="end"></slot>
    </template>
    <template v-if="showOperations" #dropdownItems>
      <EntityDropdownItems :dropdown-items="operationItems" :item="exportLog"/>
    </template>
  </VEntity>

</template>

<style scoped lang="scss">

</style>
