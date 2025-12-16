import type { NuxtError } from '#app'

export default defineNuxtPlugin((nuxtApp) => {
  nuxtApp.hook('app:error:recoverable', (error: NuxtError) => {
    useNotification().notifyError(error)
  })
})
