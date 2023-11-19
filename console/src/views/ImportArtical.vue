<script setup lang="ts">

import {Dialog} from "@halo-dev/components";
import UppyUpload from '../components/upload/UppyUpload.vue'
// import UppyUpload from "@/components/upload/UppyUpload.vue";


const onProcessCompleted = () => {
  Dialog.success({
    title:"上传",
    description: "仅支持.md文件",
    confirmText: "确定",
    showCancel: false,
    async onConfirm() {
      // await ;
    },
  });
};
</script>

<template>

  <UppyUpload
    :restrictions="{
              maxNumberOfFiles: 1,
              allowedFileTypes: ['.md'],
            }"
    endpoint="/apis/api.console.migration.halo.run/v1alpha1/restorations"
    width="100%"
    @uploaded="onProcessCompleted"
  />
</template>

<style scoped lang="scss">

</style>
