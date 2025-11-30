import { addImportsDir, addImportsSources, addPlugin, addRouteMiddleware, createResolver, defineNuxtModule, useRuntimeConfig, type Resolver } from '@nuxt/kit'
import { defu } from 'defu'

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
  setup(moduleOptions, nuxt) {
    const resolver = createResolver(import.meta.url)

    nuxt.options.runtimeConfig.public.foundation = defu(
      nuxt.options.runtimeConfig.public.foundation ?? {},
      {
        ...moduleOptions,
      },
    )

    addMiddlewares(resolver)
    addPlugins(resolver)
    addUtils(resolver)
    addStores(resolver)
  },
})

function addMiddlewares(resolver: Resolver) {
  addRouteMiddleware({
    name: '10-authentication-middleware',
    path: resolver.resolve('./runtime/middleware/10-authentication-middleware.global'),
    global: true,
  })
  addRouteMiddleware({
    name: '20-resolve-data-middleware',
    path: resolver.resolve('./runtime/middleware/20-resolve-data-middleware.global'),
    global: true,
  })
}

function addPlugins(resolver: Resolver) {
  addPlugin(resolver.resolve('./runtime/plugins/auth-session-plugin'))
  addPlugin(resolver.resolve('./runtime/plugins/error-handling-plugin'))

  addPlugin(resolver.resolve('./runtime/plugins/10-apifetch-plugin'))
  addImportsSources({
    from: resolver.resolve('./runtime/plugins/10-apifetch-plugin'),
    imports: [
      { name: '$apifetch' },
      { name: 'ApiFetch', type: true },
    ],
  })
}

function addUtils(resolver: Resolver) {
  addImportsDir(resolver.resolve('./runtime/utils'))
}

function addStores(resolver: Resolver) {
  addImportsDir(resolver.resolve('./runtime/stores'))
}
