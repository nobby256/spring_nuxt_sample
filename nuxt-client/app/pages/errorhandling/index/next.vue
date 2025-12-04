<script setup lang="ts">
import { useDataStore } from '../data-store'

definePageMeta({
  middleware: 'errorhandling-index-next-middleware',
})

const dataStore = useDataStore()
</script>

<template>
  <div>
    <h1>エラーハンドリング</h1>
    移動に成功しました。
    <div>
      {{ dataStore.value }}
    </div>
    <div>
      <nuxt-link
        to="./"
        :replace="true"
      >
        戻る
      </nuxt-link>
    </div>
  </div>
</template>
