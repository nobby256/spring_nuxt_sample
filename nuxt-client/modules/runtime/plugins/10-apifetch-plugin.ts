import type { NitroFetchRequest, NitroFetchOptions } from 'nitropack'
import type { FetchOptions, FetchHook } from 'ofetch'
import type { ModuleOptions } from '~~/modules'

export interface ApiFetch {
  <T = unknown, R extends NitroFetchRequest = NitroFetchRequest, O extends NitroFetchOptions<R> = NitroFetchOptions<R>>(request: R, opts?: O,): Promise<T>
}

export function $apifetch<
  T = unknown,
  R extends NitroFetchRequest = NitroFetchRequest,
  O extends NitroFetchOptions<R> = NitroFetchOptions<R>,
>(
  request: R,
  opts?: O,
): Promise<T> {
  return useNuxtApp().$apifetch(request, opts) as Promise<T>
}

export default defineNuxtPlugin(async () => {
  const runtimeOptions = useRuntimeConfig().public.foundation as ModuleOptions
  const baseURL = runtimeOptions.fetch?.baseURL
  const headerName = runtimeOptions.fetch?.csrfHeaderName ?? 'X-XSRF-TOKEN'
  const cookieName = runtimeOptions.fetch?.csrfCookieName ?? 'XSRF-TOKEN'

  const options: FetchOptions = {
    baseURL,
    onRequest: [
      (context) => {
        // `headers`オブジェクトが常に存在することを保証する
        const headers = context.options.headers instanceof Headers ? context.options.headers : new Headers(context.options.headers)
        context.options.headers = headers
        const token = useCookie(headerName).value
        if (token) {
          context.options.headers.set(cookieName, token)
        }
        context.options.credentials = 'include'
      },
    ] as FetchHook[],
  }

  await useNuxtApp().callHook('customize:fetch-options', options)

  return {
    provide: {
      apifetch: $fetch.create(options),
    },
  }
})
