<script setup lang="ts">

import {Toast, VAlert, VButton} from "@halo-dev/components";
import UppyUpload from '../components/upload/UppyUpload.vue'
import {ref} from 'vue'


const onProcessCompleted = () => {
  Toast.success("成功导入到草稿箱");
};
const showUploader = ref(false)
</script>

<template>

  <VAlert title="导入提示" :closable="false">
    <template #description>
      <ul>
        <li>1.仅支持导入.md文件</li>
        <li>2.导入后的文章将被保存到草稿箱，请核对后自行发布</li>
      </ul>
    </template>
    <template v-if="!showUploader" #actions>
      <VButton @click="showUploader = true">
        开始导入
      </VButton>
    </template>
  </VAlert>

  <div v-if="showUploader">
    <UppyUpload
        :restrictions="{
              allowedFileTypes: ['.md'],
            }"
        note="仅支持.md文件，可批量上传"
        endpoint="/apis/api.plugin.halo.run/v1alpha1/plugins/export2doc/doImport/import"
        width="100%"
        @uploaded="onProcessCompleted"
    />
  </div>
</template>

<style scoped lang="scss">

</style>
