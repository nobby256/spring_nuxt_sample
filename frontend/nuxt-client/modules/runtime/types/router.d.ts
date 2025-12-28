declare module 'nuxt/dist/pages/runtime/composables' {
  interface PageMeta {
    authority?: string
  }
}
declare module 'vue-router' {
  interface RouteMeta {
    authority?: string
  }
}

export {}
