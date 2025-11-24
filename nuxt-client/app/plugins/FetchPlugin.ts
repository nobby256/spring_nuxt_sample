import type { ResponseType, FetchRequest, FetchOptions, MappedResponseType } from 'ofetch'
import { ofetch } from 'ofetch'

// ofetchの関数シグネチャをコピー（Omitが使えないため、コピーするしかない）
type OFetchFunction = <T = unknown, R extends ResponseType = 'json'>(request: FetchRequest, options?: FetchOptions<R>) => Promise<MappedResponseType<R, T>>

/**
 * カスタム化したofetchをNuxtAppとしてprovideするプラグイン。
 *
 * useNuxtApp().$oetchとして利用できます。
 *
 * 以下がカスタマイズされている点です。
 * ・CSRFトークンクッキーをリクエストヘッダーに追加する
 * ・ブラウザのクッキーを有効にする
 */
export default defineNuxtPlugin((_nuxtApp) => {
  const customFetch = ofetch.create({
    onRequest(context) {
      // headersをHeadersに変換（存在しない場合は新規作成）
      const headers = context.options.headers ? new Headers(context.options.headers) : new Headers()
      context.options.headers = headers

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
      ofetch: customFetch as OFetchFunction, // useNuxtApp().$ofetch
    },
  }
})
