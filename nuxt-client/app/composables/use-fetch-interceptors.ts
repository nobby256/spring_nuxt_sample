import type { FetchHook } from 'ofetch'

/**
 * ofetch用のリクエストインターセプター（FetchHook）を生成するための
 * ファクトリー関数群を提供するコンポーザブル・スイート。
 */
export const useFetchInterceptors = () => {
  return {
    csrfTokenRequestInterceptor,
    // ... 他のインターセプターもここに追加
  }
}

interface CsrfTokenRequestInterceptorOptions {
  cookieName?: string
  headerName?: string
}
const csrfTokenRequestInterceptor = (options: CsrfTokenRequestInterceptorOptions = {}): FetchHook => {
  const {
    cookieName = 'XSRF-TOKEN',
    headerName = 'X-XSRF-TOKEN',
  } = options
  return (context) => {
    // `headers`オブジェクトが常に存在することを保証する
    const headers = context.options.headers instanceof Headers ? context.options.headers : new Headers(context.options.headers)
    context.options.headers = headers

    const token = useCookie(cookieName).value
    if (token) {
      context.options.headers.set(headerName, token)
    }
    context.options.credentials = 'include'
  }
}
