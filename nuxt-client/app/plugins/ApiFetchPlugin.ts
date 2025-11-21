export type { $Fetch, NitroFetchOptions, NitroFetchRequest } from 'nitropack'

/**
 * $fetchカスタマイズした$apiFetchをNuxtAppにprovideするプラグイン。
 *
 * 登録した$appFetchは~/utils/ApiFetch経由で使う事を想定しているため、
 * $apiFetchはグローバルで型定義を行っていません。
 */
export default defineNuxtPlugin((_nuxtApp) => {
  const apiFetch = $fetch.create({
    onRequest(context) {
      // headersをHeadersに変換（存在しない場合は新規作成）
      const headers = context.options.headers ? new Headers(context.options.headers) : new Headers()
      context.options.headers = headers

      // Acceptヘッダーを設定（ユーザー指定を尊重）
      if (!headers.has('Accept')) {
        headers.set('Accept', 'application/json')
      }

      // CSRFトークンをヘッダーに追加
      const token = useCookie('XSRF-TOKEN').value
      if (token) {
        headers.set('X-XSRF-TOKEN', token)
      }

      // ブラウザのクッキーを使用する
      context.options.credentials = 'include'
    },
  })
  return {
    provide: {
      apiFetch, // useNuxtApp()['$apiFetch']
    },
  }
})
