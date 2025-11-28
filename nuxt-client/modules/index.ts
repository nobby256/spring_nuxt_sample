import { defineNuxtModule, addRouteMiddleware, addPlugin, createResolver, addImportsDir, type Resolver } from '@nuxt/kit'

export interface ModuleOptions {
  fetch?: {
    baseURL?: string
    csrfHeaderName?: string
    csrfCookieName?: string
  }
}

export default defineNuxtModule<ModuleOptions>
({
  meta: {
    name: 'foundation',
    configKey: 'foundation',
    compatibility: {
      nuxt: '^4.0.0',
    },
  },
  defaults: {
  },
  moduleDependencies: {
    '@pinia/nuxt': {
    },
  },
  setup(_moduleOptions, _nuxt) {
    const resolver = createResolver(import.meta.url)

    addMiddlewares(resolver)
    addComposables(resolver)
    addPlugins(resolver)
    addStores(resolver)
  },
})

function addMiddlewares(resolver: Resolver) {
  addRouteMiddleware({
    name: '10-resolve-data-middleware',
    path: resolver.resolve('./runtime/middleware/10-resolve-data-middleware.global'),
    global: true,
  })
}

function addComposables(resolver: Resolver) {
  addImportsDir(resolver.resolve('./runtime/composables'))
}

function addPlugins(resolver: Resolver) {
  addPlugin(resolver.resolve('./runtime/plugins/error-handling/error-handling-plugin'))
  addPlugin(resolver.resolve('./runtime/plugins/normalize-error/normalize-error-plugin'))
  addPlugin(resolver.resolve('./runtime/plugins/ofetch/ofetch-plugin'))
  addPlugin(resolver.resolve('./runtime/plugins/apifetch/apifetch-plugin'))
}

function addStores(resolver: Resolver) {
  addImportsDir(resolver.resolve('./runtime/stores'))
}
