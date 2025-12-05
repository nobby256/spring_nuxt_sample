declare module 'nuxt/dist/pages/runtime/composables' {
  interface PageMeta {
    /**
     * 権限制御用のキー（任意）
     */
    authority?: string
  }
}

declare module 'vue-router' {
  interface RouteMeta {
    authority?: string
  }
}

export {}
