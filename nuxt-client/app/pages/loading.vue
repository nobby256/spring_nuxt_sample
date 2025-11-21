<script setup lang="ts">
definePageMeta({
  layout: false,
})

const appDataStore = useAppDataStore()

onBeforeMount(async () => {
  await appDataStore.load()
})
watch(() => appDataStore.loaded, async () => {
  // load完了後に画面遷移
  await navigateTo('/', { replace: true })
})
</script>

<template>
  <div>
    Loading...
  </div>
</template>
