<script setup lang="ts">
import { useDataStore } from './DataStore'

const dataStore = useDataStore()

onUnmounted(() => {
  dataStore.$reset()
})
</script>

<template>
  <nuxt-page />
</template>
