import type { NuxtError } from '#app'

export default defineNuxtPlugin((nuxtApp) => {
  const appStore = useAppStore()
  nuxtApp.hook('app:error:recoverable', (error: NuxtError) => {
    appStore.notifyError(error)
  })
})
