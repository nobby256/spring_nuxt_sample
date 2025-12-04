import type { RouteLocationNormalized } from 'vue-router'
import { defineNuxtRouteMiddleware, type RouteMiddleware } from '#app'

export type { RouteLocationNormalized } from 'vue-router'

export interface PreloadMiddleware {
  (to: RouteLocationNormalized, from: RouteLocationNormalized): Promise<void> | void
}

export function definePreloadMiddleware(middleware: PreloadMiddleware): RouteMiddleware {
  return defineNuxtRouteMiddleware(async (to, from) => {
    try {
      // middleware は sync/async 両対応だが、ここで一律 await して例外を拾う
      await middleware(to, from)
    }
    catch (error: unknown) {
      const nuxtError = normalizeError(error)
      if (!nuxtError.fatal) {
        await useNuxtApp().callHook('app:error:recoverable', nuxtError)
        return abortNavigation()
      }
      throw nuxtError
    }
  })
}
