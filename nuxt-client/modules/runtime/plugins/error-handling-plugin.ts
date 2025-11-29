import { showError } from '#app'

export default defineNuxtPlugin((nuxtApp) => {
  // クライアントで発生したエラーを補足するフック
  nuxtApp.hook('vue:error', async (error: unknown) => {
    const nuxtError = normalizeError(error)
    if (nuxtError.fatal) {
      // 継続不能なエラーが発生した場合はエラーページに切り替える
      showError(nuxtError)
    }
    else {
      // 継続可能なエラーが発生したことを通知
      try {
        await useNuxtApp().callHook('app:error:recoverable', nuxtError)
      }
      catch (hookError) {
        // フックの中で発生したエラーはエラーページに切り替える
        showError(normalizeError(hookError, true))
      }
    }
  })
})
