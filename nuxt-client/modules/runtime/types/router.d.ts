import 'vue-router'
import type { RouteLocationNormalized } from 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    resolveData?: (to: RouteLocationNormalized, from: RouteLocationNormalized) => Promise<void>
    authority?: string
  }
}

export {}
