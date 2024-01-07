<script setup lang="ts">
import { nextTick, ref, shallowRef } from "vue";
import ImportArtical from "@/views/ImportArtical.vue";
import ExportArtical from "@/views/ExportArtical.vue";
import { useRouteQuery } from "@vueuse/router";

import {
  IconArrowDownCircleLine,
  IconArrowUpCircleLine,
  IconArrowUpDownLine,
  VButton,
  VCard,
  VPageHeader,
  VSpace,
  VTabbar,
} from "@halo-dev/components";

const tabs = shallowRef([
  {
    id: "export2doc",
    label: "导出文章",
  },
  {
    id: "import4doc",
    label: "导入文章",
  },
]);

const activeIndex = useRouteQuery<string>("tab", tabs.value[0].id);

const export2doc = ref();
const import4doc = ref();

//新增导出
const handleCreate = () => {
  activeIndex.value = "export2doc";
  nextTick(() => {
    export2doc.value.confirmExport();
  });
};
</script>

<template>
  <VPageHeader title="文章导入导出">
    <template #icon>
      <IconArrowUpDownLine class="mr-2 self-center" />
    </template>
    <template #actions>
      <VSpace v-permission="['plugin:export2doc:manage']">
        <VButton
          type="default"
          size="sm"
          :route="{ name: 'export2doc', query: { tab: 'import4doc' } }"
        >
          <template #icon>
            <IconArrowUpCircleLine class="h-full w-full" />
          </template>
          导入文章
        </VButton>
        <VButton type="secondary" @click="handleCreate">
          <template #icon>
            <IconArrowDownCircleLine class="h-full w-full" />
          </template>
          导出文章
        </VButton>
      </VSpace>
    </template>
  </VPageHeader>

  <div class="m-0 md:m-4">
    <VCard :body-class="['!p-0']">
      <template #header>
        <VTabbar
          v-model:active-id="activeIndex"
          :items="tabs.map((item) => ({ id: item.id, label: item.label }))"
          class="w-full !rounded-none"
          type="outline"
        ></VTabbar>
      </template>
      <div class="bg-white">
        <ImportArtical v-if="activeIndex == 'import4doc'" ref="import4doc" />
        <ExportArtical v-if="activeIndex == 'export2doc'" ref="export2doc" />
      </div>
    </VCard>
  </div>
</template>
