import { useDataStore } from '../data-store'
import type { RouteLocationNormalized } from 'vue-router'

export default defineNuxtRouteMiddleware(async (to: RouteLocationNormalized, _from: RouteLocationNormalized) => {
  try {
    const value = to.query.input as string
    await useDataStore().send(value)
  }
  catch (error: unknown) {
    const nuxtError = normalizeError(error)
    // middlewareで発生した例外は強制的に継続不能扱いになってしまうため、!fatalの場合はabortNavigationする
    if (!nuxtError.fatal) {
      await useNuxtApp().callHook('app:error:recoverable', nuxtError)
      return abortNavigation()
    }
    throw nuxtError // 継続不能なエラーはそのままスローする
  }
})
