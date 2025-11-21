<script setup lang="ts">
import { useAppDataStore } from '~/stores/AppDataStore'

const appDataStore = useAppDataStore()

async function onLogout() {
  await navigateTo('/logged-out', { replace: true })
}
</script>

<template>
  <div>
    ログインユーザー:{{ appDataStore.username }}
  </div>
  <div>
    <ul>
      <li>
        <nuxt-link
          to="/errorhandling/"
          :replace="true"
        >
          エラーハンドリング
        </nuxt-link>
      </li>
    </ul>
    <ul>
      <li>
        <nuxt-link
          to="/wizard/"
          :replace="true"
        >
          画面パターン:ウィザード形式
        </nuxt-link>
      </li>
      <li>
        <nuxt-link
          to="/crud/"
          :replace="true"
        >
          画面パターン:CRUD形式
        </nuxt-link>
      </li>
    </ul>
  </div>
  <div>
    <button @click="onLogout">
      ログアウト
    </button>
  </div>
</template>
