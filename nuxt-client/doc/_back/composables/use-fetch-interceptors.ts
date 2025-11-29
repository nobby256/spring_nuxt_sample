import type { FetchHook } from 'ofetch'

export interface FetchInterceptorOptions {
  csrfCookieName?: string
  csrfHeaderName?: string
  baseURL?: string
}

export interface FetchInterceptors {
  csrfTokenRequestInterceptor: FetchHook
  baseUrlRequestInterceptor: FetchHook
  defaultRequestInterceptors: FetchHook[]
}

const defaultOptions: Required<FetchInterceptorOptions> = {
  csrfCookieName: 'XSRF-TOKEN',
  csrfHeaderName: 'X-XSRF-TOKEN',
  baseURL: '',
}

export const useFetchInterceptors = (options?: FetchInterceptorOptions): FetchInterceptors => {
  const resolvedOptions: Required<FetchInterceptorOptions> = {
    ...defaultOptions,
    ...(options ?? {}),
  }

  const csrfTokenRequestInterceptor: FetchHook = (context) => {
    // `headers`オブジェクトが常に存在することを保証する
    const headers = context.options.headers instanceof Headers ? context.options.headers : new Headers(context.options.headers)
    context.options.headers = headers

    const { csrfCookieName, csrfHeaderName } = resolvedOptions
    const token = useCookie(csrfCookieName).value
    if (token) {
      context.options.headers.set(csrfHeaderName, token)
    }
    context.options.credentials = 'include'
  }

  const baseUrlRequestInterceptor: FetchHook = (context) => {
    context.options.baseURL = resolvedOptions.baseURL
  }

  return {
    defaultRequestInterceptors: [
      baseUrlRequestInterceptor,
      csrfTokenRequestInterceptor,
    ],
    baseUrlRequestInterceptor,
    csrfTokenRequestInterceptor,
  }
}
