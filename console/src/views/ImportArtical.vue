<script setup lang="ts">

import {Toast, VAlert, VButton} from "@halo-dev/components";
import UppyUpload from '../components/upload/UppyUpload.vue'
import {ref} from 'vue'


const onProcessCompleted = () => {
  Toast.success("成功导入待发布文章列表，请自行发布");
};
const showUploader = ref(false)
</script>

<template>

  <VAlert title="导入提示" :closable="false">
    <template #description>
      <ul>
        <li>1.仅支持导入.md文件</li>
        <li>2.导入后的文章将处于 <span style="color: red">待发布状态</span>，请核对后自行发布</li>
        <li>3.默认的Markdown解析器是Markdown4j，参考资料如下：</li>
        <li><a href="https://code.google.com/archive/p/markdown4j/">https://code.google.com/archive/p/markdown4j/</a></li>
        <li><a href="https://github.com/rjeschke/txtmark">https://github.com/rjeschke/txtmark</a></li>
      </ul>
    </template>
    <template v-if="!showUploader" #actions>
      <VButton v-permission="['plugin:export2doc:manage']" @click="showUploader = true">
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
