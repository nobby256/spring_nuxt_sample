import { useFetchInterceptors } from './use-fetch-interceptors'

/**
 * カスタム化したofetchをprovideするプラグイン。
 *
 * useNuxtApp().$oetchとして利用できます。
 *
 * 以下がカスタマイズされている点です。
 * ・CSRFトークンクッキーをリクエストヘッダーに追加する
 */
export default defineNuxtPlugin((_nuxtApp) => {
  const config = useRuntimeConfig().public.foundation.fetch
  const interceptors = useFetchInterceptors()
  const apifetch = $fetch.create({
    onRequest: [
      interceptors.baseUrlRequestInterceptor({ baseURL: config.baseURL }),
      interceptors.csrfTokenRequestInterceptor({ cookieName: config.csrfCookieName, headerName: config.csrfHeaderName }),
    ],
  })
  return {
    provide: {
      // useNuxtApp().$apifetch
      apifetch,
    },
  }
})
