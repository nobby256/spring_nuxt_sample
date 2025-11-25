import { defineNuxtModule, addRouteMiddleware, addPlugin, createResolver, type Resolver } from '@nuxt/kit'

// モジュールオプションの型定義
interface ModuleOptions {
  fetch?: {
    baseURL?: string
    csrfHeaderName?: string
    csrfCookieName?: string
  }
}

// ランタイムコンフィグの型定義
interface ModuleRuntimeConfig {
  fetch: {
    baseURL: string
    csrfHeaderName: string
    csrfCookieName: string
  }
}

// モジュール定義
export default defineNuxtModule<ModuleOptions>({
  meta: {
    name: 'foundation',
    configKey: 'foundation',
    compatibility: {
      nuxt: '^4.0.0',
    },
  },
  defaults: {
    fetch: {
      baseURL: '',
      csrfHeaderName: 'X-XSRF-TOKEN',
      csrfCookieName: 'XSRF-TOKEN',
    },
  },
  setup(moduleOptions, nuxt) {
    const resolver = createResolver(import.meta.url)

    // =================================
    // ランタイムコンフィグの設定
    // =================================
    const config: ModuleRuntimeConfig = {
      fetch: {
        baseURL: moduleOptions.fetch!.baseURL!,
        csrfHeaderName: moduleOptions.fetch!.csrfHeaderName!,
        csrfCookieName: moduleOptions.fetch!.csrfCookieName!,
      },
    }
    nuxt.options.runtimeConfig.public = nuxt.options.runtimeConfig.public ?? {}
    nuxt.options.runtimeConfig.public.foundation = config

    addMiddlewares(resolver)
    addPlugins(resolver)
  },
})

function addMiddlewares(resolver: Resolver) {
  addRouteMiddleware({
    name: '10-resolve-data-middleware',
    path: resolver.resolve('./runtime/middleware/10-resolve-data-middleware.global'),
    global: true,
  })
}

function addPlugins(resolver: Resolver) {
  addPlugin(resolver.resolve('./runtime/plugins/error-handling/error-handling-plugin'))
  addPlugin(resolver.resolve('./runtime/plugins/error-normalizer/error-normalizer-plugin'))
  addPlugin(resolver.resolve('./runtime/plugins/ofetch/ofetch-plugin'))
}
