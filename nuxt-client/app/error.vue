<script setup lang="ts">
import type { NuxtError } from '#app'

const props = defineProps<{ error: NuxtError }>()

const authStore = useAuthSessionStore()
// UNAUTHORIZED(401)はセッションタイムアウト
const isSessionTimeout = props.error.statusCode === 401
if (!isSessionTimeout) {
  // error.vueは初期チャンクに含まれるため、画面表示が完全に完了する前にログアウトを行っても401は発生しません。
  await authStore.logout()
}
</script>

<template>
  <div>
    <!-- 401エラー（セッションタイムアウト）の場合 -->
    <div v-if="isSessionTimeout">
      <h1>セッションタイムアウト</h1>
      <p>セッションの有効期限が切れました。</p>
    </div>
    <!-- その他のエラーの場合 -->
    <div v-else>
      <h1>エラーが発生しました。</h1>
      <div>{{ error.message }}</div>
    </div>
    <p>ウインドウをとじてください</p>
  </div>
</template>
