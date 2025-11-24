import { ofetch } from 'ofetch'

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
      }), // useNuxtApp().$ofetch
    },
  }
})
