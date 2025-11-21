<script setup lang="ts">
import { useDataStore } from './DataStore'

// メニューによって別機能に移動する前の後処理
onUnmounted(() => {
  useDataStore().$reset()
})

// export interface KeepAliveProps {
//     include?: MatchPattern;
//     exclude?: MatchPattern;
//     max?: number | string;
// }
const keepAliveProps = {
  include: ['index'], // CRUDのような機能の場合は末端の画面はキャッシュに含めないのがコツ
}
</script>

<template>
  <!-- index.vueのみをキャッシュに格納します -->
  <nuxt-page :keepalive="keepAliveProps" />
</template>
