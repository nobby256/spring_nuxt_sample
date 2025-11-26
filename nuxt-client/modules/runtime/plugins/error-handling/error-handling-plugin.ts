import { showError } from '#app'
import { useNormalizeError } from '../../composables/use-normalize-error'

export default defineNuxtPlugin((nuxtApp) => {
  // クライアントで発生したエラーを補足するフック
  nuxtApp.hook('vue:error', async (error: unknown) => {
    const nuxtError = useNormalizeError().normalize(error)
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
