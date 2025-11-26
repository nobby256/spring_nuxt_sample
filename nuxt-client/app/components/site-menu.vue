<script setup lang="ts">
const { username } = useAppStore()

async function onLogout() {
  await navigateTo('/logout', { replace: true })
}
</script>

<template>
  <div>
    ログインユーザー:{{ username }}
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
