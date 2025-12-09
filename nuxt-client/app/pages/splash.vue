<script setup lang="ts">
definePageMeta({
  layout: false,
})

const { load, $id } = useAppStore()

const { pending, error } = useAsyncData(
  $id,
  () => load(),
)

// 読み込み完了を待機
watch(pending, async (newValue) => {
  if (newValue === false) {
    await navigateTo('/', { replace: true })
  }
}, { immediate: true })
// エラー発生を待機
watch(error, (newValue) => {
  if (newValue) {
    const nuxtError = normalizeError(newValue)
    // 初期データ取得でのエラーは継続不能エラーとする
    nuxtError.fatal = true
    throw nuxtError
  }
}, { immediate: true })
</script>

<template>
  <span>Loading...</span>
</template>
