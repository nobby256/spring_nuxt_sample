<script setup lang="ts">
const authStore = useAuthenticationStore()

onBeforeMount(async () => {
  if (!authStore.loaded) {
    await authStore.load()
  }
})
</script>

<template>
  <div v-if="!authStore.loaded">
    <span>Loading...</span>
  </div>
  <div v-else>
    <nuxt-notifications />
    <nuxt-layout>
      <nuxt-page />
    </nuxt-layout>
  </div>
</template>
