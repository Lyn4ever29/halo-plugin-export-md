<script setup lang="ts">
import confetti from "canvas-confetti";
import {onMounted, ref} from "vue";
import CarbonPackage from '~icons/carbon/package'
import AntDesignInfoCircleOutlined from '~icons/ant-design/info-circle-outlined'
import AboutMe from "@/components/AboutMe.vue";
import ExportArtical from "@/components/ExportArticalV1.vue";

onMounted(() => {
  confetti({
    particleCount: 100,
    spread: 70,
    origin: {y: 0.6, x: 0.58},
  });
});

const activeIndex = ref('1')
const handleSelect = (key: string, keyPath: string[]) => {
  console.log(key, keyPath)
  activeIndex.value = keyPath[0]
  console.log(activeIndex.value);
}
</script>

<template>
  <div class="common-layout" id="plugin-export-anything">
    <el-container>
      <el-header>
        <el-menu
            :default-active="activeIndex"
            class="el-menu-demo"
            mode="horizontal"
            @select="handleSelect"
        >
          <el-menu-item index="1" >
            <CarbonPackage/>
            <span>导出文章</span>
          </el-menu-item>
          <!--          <el-menu-item index="2">-->
          <!--            <MdiCalendarImportOutline/>-->
          <!--            <span>导入文档</span>-->
          <!--          </el-menu-item>-->
          <el-menu-item index="4" >
            <AntDesignInfoCircleOutlined/>
            <span>关于</span>
          </el-menu-item>
          <!--          <el-menu-item index="5">-->
          <!--            <MaterialSymbolsTipsAndUpdatesOutline/>-->
          <!--            <span>更新日志</span>-->
          <!--          </el-menu-item>-->
        </el-menu>
      </el-header>
      <el-main>
        <!--主内容-->

        <!--文档导出-->
        <ExportArtical v-if="activeIndex==1"/>

        <!--文档导出-->
        <AboutMe v-if="activeIndex==4"/>


      </el-main>
      <!--      <el-footer>Footer</el-footer>-->
    </el-container>
  </div>
</template>

<style lang="scss" scoped>
@import 'element-plus/dist/index.css';

#plugin-export-anything {
  height: 100vh;
  background-color: #f8fafc;
}

.wrapper {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  height: 100vh;
  gap: 1.5rem;

  .title {
    font-weight: 700;
    font-size: 1.25rem;
    line-height: 1.75rem;
  }

  .message {
    font-size: 0.875rem;
    line-height: 1.25rem;
    color: #4b5563;
  }

  .docs {
    display: grid;
    grid-template-columns: repeat(1, minmax(0, 1fr));
    gap: 1rem;
    max-width: 48rem;

    .docs__box {
      background-color: #fff;
      border-radius: 0.375rem;
      padding: 0.75rem;
      transition-property: all;
      transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
      transition-duration: 300ms;
      cursor: pointer;
      filter: drop-shadow(0 1px 2px rgb(0 0 0 / 0.1)) drop-shadow(0 1px 1px rgb(0 0 0 / 0.06));

      &:hover {
        box-shadow: 0 0 0 0px #fff, 0 0 0 1px rgb(59 130 246 / 0.5), 0 0 #0000;
      }

      .docs__box-title {
        display: flex;
        flex-direction: row;
        font-size: 1.125rem;
        line-height: 1.75rem;
        font-weight: 700;
        margin-bottom: 2rem;
        gap: 0.5rem;
        align-items: center;
      }

      .docs__box-message {
        font-size: 0.875rem;
        line-height: 1.25rem;
        color: #4b5563;
      }

      .docs__box-arrow {
        pointer-events: none;
        position: absolute;
        top: 1rem;
        right: 1rem;
        transition-property: all;
        transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
        transition-duration: 150ms;
        color: #d1d5db;
      }

      &:hover {
        .docs__box-arrow {
          color: #9ca3af;
          transform: translate(00.375rem, 0) rotate(0) skewX(0) skewY(0) scaleX(1) scaleY(1);
        }
      }
    }
  }

  @media (min-width: 640px) {
    .docs {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
  }
}
</style>
