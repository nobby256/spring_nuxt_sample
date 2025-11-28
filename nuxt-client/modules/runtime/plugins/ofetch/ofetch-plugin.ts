import { ofetch } from 'ofetch'
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
export default defineNuxtPlugin(() => {
  const options = useRuntimeConfig().public.foundation as ModuleOptions
  const baseURL = options?.fetch?.baseURL
  const headerName = options?.fetch?.csrfHeaderName
  const cookieName = options?.fetch?.csrfCookieName

  const interceptors = useFetchInterceptors()
  return {
    provide: {
      // useNuxtApp().$ofetch
      ofetch: ofetch.create({
        onRequest: [
          interceptors.baseUrlRequestInterceptor({ baseURL }),
          interceptors.csrfTokenRequestInterceptor({ cookieName, headerName }),
        ],
      }),
    },
  }
})
