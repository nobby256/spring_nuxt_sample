<script setup lang="ts">
import { useDataStore } from './data-store'

definePageMeta({
  middleware: 'errorhandling-index.entry-middleware',
})

const dataStore = useDataStore()

onUnmounted(() => {
  dataStore.$reset()
})
</script>

<template>
  <nuxt-page />
</template>
