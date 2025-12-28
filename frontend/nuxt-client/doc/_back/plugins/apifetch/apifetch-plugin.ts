import { useFetchInterceptors } from './use-fetch-interceptors'
import type { ModuleOptions } from '~~/modules'
/**
 * カスタム化したofetchをprovideするプラグイン。
 *
 * useNuxtApp().$oetchとして利用できます。
 *
 * 以下がカスタマイズされている点です。
 * ・CSRFトークンクッキーをリクエストヘッダーに追加する
 */
export default defineNuxtPlugin((_nuxtApp) => {
  const runtimeConfig = useRuntimeConfig().public.foundation as ModuleOptions
  const baseURL = runtimeConfig.fetch?.baseURL
  const headerName = runtimeConfig.fetch?.csrfHeaderName
  const cookieName = runtimeConfig.fetch?.csrfCookieName

  const interceptors = useFetchInterceptors()
  const apifetch = $fetch.create({
    onRequest: [
      interceptors.baseUrlRequestInterceptor({ baseURL }),
      interceptors.csrfTokenRequestInterceptor({ cookieName, headerName }),
    ],
  })
  return {
    provide: {
      // useNuxtApp().$apifetch
      apifetch,
    },
  }
})
