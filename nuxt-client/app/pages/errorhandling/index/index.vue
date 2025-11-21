<script setup lang="ts">
import { useDataStore } from '../DataStore'

// ==============================================================
// 変数
// ==============================================================
const dataStore = useDataStore()
const inputValue = ref('')

// ==============================================================
// 関数
// ==============================================================
async function onClick() {
  const result = await dataStore.send(inputValue.value)
  // 呼び出し成功のケース
  alert(JSON.stringify(result))
}
</script>

<template>
  <div>
    <h1>エラーハンドリング</h1>
    <fieldset>
      <legend>送信項目</legend>
      <div>
        <label>項目
          <input
            v-model="inputValue"
            type="text"
          >
        </label>
        <button
          type="submit"
          @click="onClick"
        >
          送信
        </button>
      </div>
    </fieldset>

    <fieldset>
      <legend>送信項目の説明</legend>
      <table border="1">
        <thead>
          <tr>
            <th>ルール</th>
            <th>レスポンス</th>
            <th>サンプル値</th>
            <th />
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>文字列長が０</td>
            <td>BAD_REQUEST(400)<br>入力チェックエラーなど。不具合と解釈</td>
            <td>(空文字)</td>
            <td>
              <button
                type="button"
                @click="inputValue = ''"
              >
                設定
              </button>
            </td>
          </tr>
          <tr>
            <td>文字列長が１</td>
            <td>業務例外<br>content-typeがapplication/problem+json。<br>ステータスコード/レスポンスボディは不問。</td>
            <td>a</td>
            <td>
              <button
                type="button"
                @click="inputValue = 'a'"
              >
                設定
              </button>
            </td>
          </tr>
          <tr>
            <td>文字列長が２</td>
            <td>INTERNAL SERVER ERROR(500)</td>
            <td>ab</td>
            <td>
              <button
                type="button"
                @click="inputValue = 'ab'"
              >
                設定
              </button>
            </td>
          </tr>
          <tr>
            <td>文字列長が３</td>
            <td>UNAUTHORIZED(401)<br>未認証かセッションタイムアウト</td>
            <td>abc</td>
            <td>
              <button
                type="button"
                @click="inputValue = 'abc'"
              >
                設定
              </button>
            </td>
          </tr>
          <tr>
            <td>文字列長が４</td>
            <td>FORBIDDEN(403)<br>認証済みだが権限がない</td>
            <td>abcd</td>
            <td>
              <button
                type="button"
                @click="inputValue = 'abcd'"
              >
                設定
              </button>
            </td>
          </tr>
          <tr>
            <td>文字列長が５</td>
            <td>NOT FOUND(404)<br>URL間違い。不具合。</td>
            <td>abcde</td>
            <td>
              <button
                type="button"
                @click="inputValue = 'abcde'"
              >
                設定
              </button>
            </td>
          </tr>
          <tr>
            <td>文字列長が６</td>
            <td>OK（200）</td>
            <td>abcdefg</td>
            <td>
              <button
                type="button"
                @click="inputValue = 'abcdef'"
              >
                設定
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </fieldset>
  </div>
</template>
