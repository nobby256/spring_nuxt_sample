export default defineNuxtPlugin(async (nuxtApp) => {
  nuxtApp.hook('openFetch:onRequest', (context) => {
    // `headers`オブジェクトが常に存在することを保証する
    const headers = context.options.headers instanceof Headers ? context.options.headers : new Headers(context.options.headers)
    context.options.headers = headers
    const token = useCookie('XSRF-TOKEN').value
    if (token) {
      context.options.headers.set('X-XSRF-TOKEN', token)
    }
    context.options.credentials = 'include'
  })
})
