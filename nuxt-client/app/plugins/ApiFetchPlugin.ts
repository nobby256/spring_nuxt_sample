import { normalizeError } from '~/utils/Errors'
import type { NitroFetchRequest, NitroFetchOptions } from 'nitropack'

export type ApiFetch = <T = unknown>(
  request: NitroFetchRequest,
  options?: NitroFetchOptions<NitroFetchRequest>,
) => Promise<T>

/**
 * $fetchカスタマイズした$apiFetchをNuxtAppにprovideするプラグイン。
 *
 * 登録した$appFetchは~/utils/ApiFetch経由で使う事を想定しているため、
 * $apiFetchはグローバルで型定義を行っていません。
 */
export default defineNuxtPlugin((nuxtApp) => {
  const delegateFetch = $fetch.create({
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

  // delegateFetchの例外を変更する為にラップする
  const apiFetch: ApiFetch = async (request, options) => {
    // サーバーのベースURLをruntimeConfigから取得する
    const baseURL = useRuntimeConfig().public?.serverOrigin || ''
    try {
      return await delegateFetch(request, { baseURL, ...options })
    }
    catch (error) {
      // 発生したエラーをNuxtErrorに正規化し、401/403エラーの場合にfatalエラーとする
      const nuxtError = normalizeError(error)
      if (nuxtError.statusCode === 401 || nuxtError.statusCode === 403) {
        nuxtError.fatal = true
      }
      // フックを呼び出すことでErrorをカスタマイズ可能にする
      // 例：fatalエラーにするルールを変更する等
      await nuxtApp.callHook('customize:api-error', nuxtError)
      throw nuxtError
    }
  }

  return {
    provide: {
      apiFetch, // useNuxtApp().$apiFetch $が付く名前で提供する
    },
  }
})
