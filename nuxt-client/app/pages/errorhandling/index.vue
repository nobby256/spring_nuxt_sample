<script setup lang="ts">
import { useDataStore } from './data-store'

const dataStore = useDataStore()

onUnmounted(() => {
  dataStore.$reset()
})
</script>

<template>
  <nuxt-page />
</template>
