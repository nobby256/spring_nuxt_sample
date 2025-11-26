<script setup lang="ts">
const { loaded, load } = useAppStore()
if (!loaded) {
  await load()
}
</script>

<template>
  <div v-if="!loaded">
    <span>Loading...</span>
  </div>
  <div v-else>
    <nuxt-notifications />
    <nuxt-layout>
      <nuxt-page />
    </nuxt-layout>
  </div>
</template>
