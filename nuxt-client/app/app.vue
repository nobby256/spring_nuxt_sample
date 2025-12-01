<script setup lang="ts">
const { load, $id } = useAppStore()

const { pending, error } = await useAsyncData(
  $id,
  () => load(),
)
watch(error, (errorValue) => {
  if (errorValue) {
    throw errorValue
  }
})
</script>

<template>
  <div v-if="pending">
    <span>Loading...</span>
  </div>
  <div v-else>
    <nuxt-notifications />
    <nuxt-layout>
      <nuxt-page />
    </nuxt-layout>
  </div>
</template>
