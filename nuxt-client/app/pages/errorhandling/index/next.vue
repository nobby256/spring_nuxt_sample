<script setup lang="ts">
import { useDataStore } from '../data-store'
import type { RouteLocationNormalized } from 'vue-router'

const resultValue = ref('')

const resolveData = async (to: RouteLocationNormalized, _from: RouteLocationNormalized) => {
  const value = to.query.input as string
  const dataStore = useDataStore()
  const result = await dataStore.send(value)
  alert('resolveData' + JSON.stringify(result))
  resultValue.value = JSON.stringify(result)
}

definePageMeta({
  resolveData: resolveData,
  // resolveData: async (to: RouteLocationNormalized) => {
  //   const value = to.query.input as string
  //   const dataStore = useDataStore()
  //   const result = await dataStore.send(value)
  //   alert('resolveData' + JSON.stringify(result))
  //   resultValue.value = JSON.stringify(result)
  // },
})
</script>

<template>
  <div>
    <h1>エラーハンドリング</h1>
    移動に成功しました。
    <div>
      {{ resultValue }}
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
