import type { RouteLocationNormalized } from 'vue-router'

declare module 'nuxt/dist/pages/runtime/composables' {
  interface PageMeta {
    /**
     * ページ用のデータ解決フックをトップレベルで指定します。
     * 例: definePageMeta({ resolveData: (to, from) => Promise<{ foo: string }> })
     * 戻り値は middleware で直接利用していないため汎用的に unknown を許容します。
     */
    resolveData?: (to: RouteLocationNormalized, from: RouteLocationNormalized) => Promise<unknown> | unknown

    /**
     * 権限制御用のキー（任意）
     */
    authority?: string
  }
}

declare module 'vue-router' {
  interface RouteMeta {
    // RouteMeta は UnwrapRef<PageMeta> を継承するため、トップレベルの resolveData が反映されます
    resolveData?: (to: RouteLocationNormalized, from: RouteLocationNormalized) => Promise<unknown> | unknown
    authority?: string
  }
}

export {}
