<script setup lang="ts">
const { value, errorMessage } = useField('input2')
</script>

<template>
  <fieldset>
    <legend>ページ２</legend>
    <label>
      入力値２
      <input
        v-model="value"
        type="text"
      >
    </label>
    <div>{{ errorMessage }}</div>
  </fieldset>
</template>
