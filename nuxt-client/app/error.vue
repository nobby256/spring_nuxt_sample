<script setup lang="ts">
import type { NuxtError } from '#app'

const props = defineProps<{ error: NuxtError }>()

// UNAUTHORIZED(401)かつ、データがロード済み、はセッションタイムアウト
const isSessionTimeout = props.error.statusCode === 401 && useAuthenticationStore().loaded
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
