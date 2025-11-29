import { showError } from '#app'

export default defineNuxtPlugin((nuxtApp) => {
  // クライアントで発生したエラーを補足するフック
  // plugin中にエラーが発生するとvue:errorではなくapp:errorが呼ばれるので、
  // このプラグインの優先度は特別高くしなくても良い
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
