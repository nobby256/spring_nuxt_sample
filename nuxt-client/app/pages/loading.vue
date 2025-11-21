<script setup lang="ts">
definePageMeta({
  layout: false,
})

const authStore = useAuthenticationStore()

onBeforeMount(async () => {
  await authStore.load()
})
watch(() => authStore.loaded, async () => {
  // load完了後に画面遷移
  await navigateTo('/', { replace: true })
})
</script>

<template>
  <div>
    Loading...
  </div>
</template>
