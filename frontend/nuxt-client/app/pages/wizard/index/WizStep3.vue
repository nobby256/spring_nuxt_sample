<script setup lang="ts">
const { value, errorMessage } = useField('input3')
</script>

<template>
  <fieldset>
    <legend>ページ３</legend>
    <label>
      入力値３
      <input
        v-model="value"
        type="text"
      >
    </label>
    <div>{{ errorMessage }}</div>
  </fieldset>
</template>
