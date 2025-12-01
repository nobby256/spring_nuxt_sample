<script setup lang="ts">
import type { NuxtError } from '#app'

const props = defineProps<{ error: NuxtError }>()

let reason: 'unauthenticated' | 'session-timeout' | 'other' = 'other'

const authStore = useAuthSessionStore()
if (props.error.statusCode === 401) {
  if (authStore.user) {
    reason = 'session-timeout'
  }
  else {
    reason = 'unauthenticated'
  }
}
else {
  // error.vueは初期チャンクに含まれるため、画面表示が完全に完了する前にログアウトを行っても401は発生しません。
  await authStore.logout()
}
</script>

<template>
  <div>
    <div v-if="reason == 'unauthenticated'">
      <h1>認証エラー</h1>
      <p>ログインを行ってください。</p>
    </div>
    <div v-else-if="reason == 'session-timeout'">
      <h1>セッションタイムアウト</h1>
      <p>セッションの有効期限が切れました。</p>
    </div>
    <!-- その他のエラーの場合 -->
    <div v-else>
      <h1>エラーが発生しました。status({{ error.statusCode }})</h1>
      <div>{{ error.message }}</div>
    </div>
    <p>ウインドウをとじてください</p>
  </div>
</template>
