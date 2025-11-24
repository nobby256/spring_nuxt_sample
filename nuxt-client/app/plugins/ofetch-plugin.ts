import type { ResponseType, FetchRequest, FetchOptions, MappedResponseType } from 'ofetch'
import { ofetch } from 'ofetch'

// ofetchの関数シグネチャをコピー（Omitが使えないため、コピーするしかない）
type OFetchFunction = <T = unknown, R extends ResponseType = 'json'>(request: FetchRequest, options?: FetchOptions<R>) => Promise<MappedResponseType<R, T>>

/**
 * カスタム化したofetchをprovideするプラグイン。
 *
 * useNuxtApp().$oetchとして利用できます。
 *
 * 以下がカスタマイズされている点です。
 * ・CSRFトークンクッキーをリクエストヘッダーに追加する
 */
export default defineNuxtPlugin((_nuxtApp) => {
  const interceptors = useFetchInterceptors()
  return {
    provide: {
      ofetch: ofetch.create({
        onRequest: [
          interceptors.csrfTokenRequestInterceptor(),
        ],
      }) as OFetchFunction, // useNuxtApp().$ofetch
    },
  }
})
