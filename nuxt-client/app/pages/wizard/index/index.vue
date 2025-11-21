<script setup lang="ts">
import * as yup from 'yup'
import { usePager } from '../usePager'
import { useDataStore } from '../DataStore'
import { useFormState } from '../FormState'
import WizStep1 from './WizStep1.vue'
import WizStep2 from './WizStep2.vue'
import WizStep3 from './WizStep3.vue'

// ==============================================================
// 変数
// ==============================================================
const dataStore = useDataStore()
const formState = useFormState()
const pager = usePager(3)
const isFinish = ref(false)

// ==============================================================
// フォーム
// ==============================================================
// ルールの定義
const formSchema = [
  // ステップ１用のルール
  yup.object({
    input1: yup.string().required(),
  }),
  // ステップ２用のルール
  yup.object({
    input2: yup.string().required(),
  }),
  // ステップ３用のルール
  yup.object({
    input3: yup.string().required(),
  }),
]

// フォームの定義
const {
  handleSubmit,
  values,
  errors,
  meta,
} = useForm({
  // 現在表示されているステップによってルールを切り替える
  validationSchema: computed(() => formSchema[pager.pageNo.value - 1]),
  // フィールドがunmountされても値を保持する
  keepValuesOnUnmount: true,
  // 状態保持オブジェクトからの復旧
  initialValues: {
    input1: formState.input1,
    input2: formState.input2,
    input3: formState.input3,
  },
})
// フォームの入力値を監視し、変更された値のみを状態保持オブジェクトに退避する
watch(values, (newValue) => {
  formState.$patch(newValue)
})

// ==============================================================
// 関数
// ==============================================================

// ページ遷移前の確認を行う
onBeforeRouteLeave((to, from, next) => {
  let leave = true
  if (!isFinish.value && meta.value.dirty) {
    leave = confirm('このページを離れますか？離れても入力内容は維持されます。')
  }
  next(leave)
})

// 前のステップに戻る
const onPrev = () => {
  pager.prevPage()
}

// 次のステップに進む（前進時は入力チェックを伴う）
const onNext = handleSubmit(async (values) => {
  if (pager.hasNextPage()) {
    pager.nextPage()
  }
  else {
    await dataStore.post(values)
    isFinish.value = true
    // 状態保持オブジェクトのクリア
    formState.$reset()
  }
})
</script>

<template>
  <div>
    <h1>ウィザード形式</h1>

    <div v-if="isFinish">
      登録完了。
    </div>

    <div v-else>
      <div>
        <button
          type="button"
          :disabled="!pager.hasPrevPage()"
          @click="onPrev"
        >
          前の画面
        </button>
        <button
          type="button"
          @click="onNext"
        >
          {{ pager.hasNextPage()?'次の画面':'登録' }}
        </button>
      </div>
      <wiz-step1 v-if="pager.pageNo.value === 1" />
      <wiz-step2 v-if="pager.pageNo.value === 2" />
      <wiz-step3 v-if="pager.pageNo.value === 3" />
    </div>
    <fieldset>
      <legend>デバッグ：フォームの状態</legend>
      <div>
        values:{{ values }}
      </div>
      <div>
        errors:{{ errors }}
      </div>
      <div>
        meta:{{ meta }}
      </div>
    </fieldset>
  </div>
</template>
