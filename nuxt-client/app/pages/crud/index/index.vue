<script setup lang="ts">
import { useDataStore } from '../DataStore'

// ==============================================================
// 変数
// ==============================================================
const dataStore = useDataStore()
// 検索条件
const name = ref<string | undefined>()

// ==============================================================
// 関数
// ==============================================================
const onSearch = async () => {
  await dataStore.search({ name: name.value })
}
</script>

<template>
  <div>
    <h1>CRUD形式</h1>

    <fieldset>
      <legend>商品検索</legend>
      <label>商品名
        <input
          v-model="name"
          type="text"
          name="name"
        >
      </label>
      <button
        type="button"
        @click="onSearch"
      >
        検索
      </button>
    </fieldset>

    <table border="1">
      <thead>
        <tr>
          <th>商品コード</th>
          <th>商品名</th>
          <th>メーカー</th>
          <th>単価</th>
          <th>アクション</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="item in dataStore.items"
          :key="item.id"
        >
          <td>{{ item.id }}</td>
          <td>{{ item.name }}</td>
          <td>{{ item.maker }}</td>
          <td>{{ item.price }}円</td>
          <td>
            <nuxt-link
              :to="`./${item.id}`"
              :replace="true"
            >
              編集
            </nuxt-link>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
