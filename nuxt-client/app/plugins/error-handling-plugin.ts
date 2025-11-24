import { showError } from '#app'

export default defineNuxtPlugin((nuxtApp) => {
  nuxtApp.hook('vue:error', async (error: unknown) => {
    const nuxtError = normalizeError(error)
    if (nuxtError.fatal) {
      // 継続不能なエラーが発生した場合はエラーページに切り替える
      showError(nuxtError)
    }
    else {
      // 継続可能なエラーが発生したことを通知
      await useNuxtApp().callHook('app:error:recoverable', nuxtError)
    }
  })
})
