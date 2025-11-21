<script setup lang="ts">
import * as yup from 'yup'
import { useDataStore } from '../DataStore'

// ==============================================================
// 変数
// ==============================================================
const dataStore = useDataStore()

// ==============================================================
// フォーム
// ==============================================================
// ルールの定義
const formSchema = yup.object({
  name: yup.string().required(),
  price: yup.number().required(),
  maker: yup.string().required(),
  description: yup.string().optional(),
})

// フォームの定義
const {
  defineField,
  setValues,
  errors,
  handleSubmit,
  isSubmitting,
} = useForm({
  validationSchema: toTypedSchema(formSchema),
})

// フィールドの定義
const [name] = defineField('name')
const [price] = defineField('price')
const [maker] = defineField('maker')
const [description] = defineField('description')

// ==============================================================
// 関数
// ==============================================================
const onUpdate = handleSubmit(async (values) => {
  await dataStore.update({ id, ...values })
  await navigateTo('./', { replace: true })
})

const onCancel = async () => {
  await navigateTo('./', { replace: true })
}
// ==============================================================
// 初期処理
// ==============================================================
const id = useRoute().params.id as string

// １件取得
const item = await dataStore.get(id)

// フィールドに値をセット
setValues({
  name: item.name,
  price: item.price,
  maker: item.maker,
  description: item.description,
})
</script>

<template>
  <div>
    <fieldset>
      <legend>商品登録</legend>
      <div>
        <label>
          商品コード
          <input
            :value="id"
            readonly="true"
          >
        </label>
      </div>
      <div>
        <label>
          <span>商品名</span>
          <input v-model="name">
          <div>{{ errors.name }}</div>
        </label>
      </div>
      <div>
        <label>
          <span>メーカー</span>
          <input v-model="maker">
        </label>
      </div>
      <div>
        <label>
          <span>単価</span>
          <input v-model="price">
          <div>{{ errors.price }}</div>
        </label>
      </div>
      <div>
        <label>
          <span>補足事項</span>
          <input v-model="description">
          <div>{{ errors.description }}</div>
        </label>
      </div>
      <div>
        <button
          type="button"
          :disabled="isSubmitting"
          @click="onUpdate"
        >
          更新
        </button>
        <button
          type="button"
          :disabled="isSubmitting"
          @click="onCancel"
        >
          キャンセル
        </button>
      </div>
    </fieldset>
  </div>
</template>

<style>
input[readonly] {
  background-color: silver;
}
</style>
