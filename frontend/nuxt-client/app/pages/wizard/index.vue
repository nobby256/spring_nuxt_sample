<script setup lang="ts">
import { useDataStore } from './data-store'

// ==============================================================
// 機能で利用するリードオンリーデータの読み込み

// ==============================================================
// 機能の後処理
onUnmounted(() => {
  useDataStore().$reset()
})
</script>

<template>
  <nuxt-page />
</template>
