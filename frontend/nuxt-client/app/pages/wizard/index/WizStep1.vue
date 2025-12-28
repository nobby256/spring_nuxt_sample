<script setup lang="ts">
const { value, errorMessage } = useField('input1')
</script>

<template>
  <fieldset>
    <legend>ページ１</legend>
    <label>
      入力値１
      <input
        v-model="value"
        type="text"
      >
    </label>
    <div>{{ errorMessage }}</div>
  </fieldset>
</template>
