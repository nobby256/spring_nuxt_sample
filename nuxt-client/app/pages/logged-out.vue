<script setup lang="ts">
definePageMeta({
  layout: false,
})

onMounted(async () => {
  // onMountedでログアウトを行う理由
  // このベージは認証が必要なベージなので、ベージの構成要素（.js,.cssなど）が完全に呼び出された後に
  // ログアウトを呼び出す必要性があります。
  // なお、imageタグのsrcは非同期のダウンロードとなるため、onMountedではダウンロード完了を保証できません。
  // そのため、imageタグが利用している画像は認証不要のフォルダ（例：/images/**）に格納してください。
  const appDataStore = useAppDataStore()
  await appDataStore.logout()
})
</script>

<template>
  <div>
    <h1>ログアウトしました</h1>
    <p>ウインドウをとじてください</p>
  </div>
</template>
