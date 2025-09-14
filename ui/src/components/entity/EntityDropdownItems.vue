<script setup lang="ts" generic="T">
import type { OperationItem } from "@halo-dev/console-shared";
import { VDropdown } from "@halo-dev/components";

const props = withDefaults(
  defineProps<{
    dropdownItems: OperationItem<T>[];
    item?: T;
  }>(),
  {
    item: undefined,
  }
);

function action(dropdownItem: OperationItem<T>) {
  if (!dropdownItem.action) {
    return;
  }
  dropdownItem.action(props.item);
}
</script>

<template>
  <template v-for="(dropdownItem, index) in dropdownItems">
    <template v-if="!dropdownItem.hidden">
      <VDropdown
        v-if="dropdownItem.children?.length"
        :key="`dropdown-children-items-${index}`"
        :triggers="['click']"
      >
        <component
          :is="dropdownItem.component"
          v-bind="dropdownItem.props"
          @click="action(dropdownItem)"
        >
          {{ dropdownItem.label }}
        </component>
        <template #popper>
          <template v-for="(childItem, childIndex) in dropdownItem.children">
            <component
              :is="childItem.component"
              v-if="!childItem.hidden"
              v-bind="childItem.props"
              :key="`dropdown-child-item-${childIndex}`"
              @click="action(childItem)"
            >
              {{ childItem.label }}
            </component>
          </template>
        </template>
      </VDropdown>
      <component
        :is="dropdownItem.component"
        v-else
        v-bind="dropdownItem.props"
        :key="`dropdown-item-${index}`"
        v-permission="['plugin:export2doc:manage']"
        @click="action(dropdownItem)"
      >
        {{ dropdownItem.label }}
      </component>
    </template>
  </template>
</template>
