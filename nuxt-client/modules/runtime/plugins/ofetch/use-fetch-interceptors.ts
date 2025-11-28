import type { FetchHook } from 'ofetch'

/**
 * ofetch用のリクエストインターセプター（FetchHook）を生成するための
 * ファクトリー関数群を提供するコンポーザブル・スイート。
 */
export const useFetchInterceptors = () => {
  return {
    baseUrlRequestInterceptor,
    csrfTokenRequestInterceptor,
    // ... 他のインターセプターもここに追加
  }
}

interface CsrfTokenRequestInterceptorOptions {
  cookieName?: string
  headerName?: string
}
const csrfTokenRequestInterceptor = (options?: CsrfTokenRequestInterceptorOptions): FetchHook => {
  return (context) => {
    // `headers`オブジェクトが常に存在することを保証する
    const headers = context.options.headers instanceof Headers ? context.options.headers : new Headers(context.options.headers)
    context.options.headers = headers

    const cookieName = options?.cookieName ?? 'XSRF-TOKEN'
    const headerName = options?.headerName ?? 'X-XSRF-TOKEN'

    const token = useCookie(cookieName).value
    if (token) {
      context.options.headers.set(headerName, token)
    }
    context.options.credentials = 'include'
  }
}

interface BaseUrlRequestInterceptorOptions {
  baseURL?: string
}
const baseUrlRequestInterceptor = (options: BaseUrlRequestInterceptorOptions): FetchHook => {
  return (context) => {
    context.options.baseURL = options.baseURL
  }
}
