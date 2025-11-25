import { readdirSync } from 'fs'
import { defineNuxtModule, addRouteMiddleware, addPlugin, createResolver, addImportsDir, type Resolver } from '@nuxt/kit'

// モジュールオプションの型定義
export interface ModuleOptions {
  fetch?: {
    baseURL?: string
    csrfHeaderName?: string
    csrfCookieName?: string
  }
}

// ランタイムコンフィグの型定義
export interface ModuleRuntimeConfig {
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

    // =================================
    // ミドルウェアの登録
    // =================================
    addMiddlewares(resolver)

    // =================================
    // プラグインの登録
    // =================================
    addPlugins(resolver)

    // =================================
    // コンポーザブルの登録
    // =================================
    // addComposables(resolver)

    // =================================
    // ユーティリティを自動インポートとして登録
    // =================================
    // addUtils(resolver)
  },
})

function addMiddlewares(resolver: Resolver) {
  addRouteMiddleware({
    name: '10-resolve-data-middleware',
    path: resolver.resolve('./runtime/middleware/10-resolve-data-middleware.global'),
    global: true,
  })
}

function getJsModuleNames(targetDir: string): string[] {
  const jsModuleNames = readdirSync(targetDir)
    .filter((file: string) => file.endsWith('.ts') && !file.endsWith('.d.ts'))
    .map((file: string) => file.replace('.ts', ''))
  return jsModuleNames
}

function addPlugins(resolver: Resolver) {
  // プラグインディレクトリ内のすべての.tsファイルを自動で登録
  // const jsModuleNames = getJsModuleNames(resolver.resolve('./runtime/plugins'))
  // jsModuleNames.forEach((pluginName: string) => {
  //   addPlugin(resolver.resolve(`./runtime/plugins/${pluginName}`))
  // })

  // モジュールオプションを利用する等して個別に追加する場合
  addPlugin(resolver.resolve('./runtime/plugins/error-handling/error-handling-plugin'))
  addPlugin(resolver.resolve('./runtime/plugins/error-normalizer/error-normalizer-plugin'))
  addPlugin(resolver.resolve('./runtime/plugins/ofetch/ofetch-plugin'))
}

function addComposables(resolver: Resolver) {
  addImportsDir(resolver.resolve('./runtime/composables'))
}

function addUtils(resolver: Resolver) {
  // utilsフォルダのtsと、そのexportをすべて.nuxt/imports.d.tsに追加
  addImportsDir(resolver.resolve('./runtime/utils'))

  // 個別で指定する場合は下記の通り
  // addImportsSources({
  //   from: resolver.resolve('./runtime/utils/BffFetch'),
  //   imports: [
  //     { name: 'bffFetch' },
  //     { name: 'NitroFetchOptions', type: true },
  //     { name: 'NitroFetchRequest', type: true },
  //     { name: 'TypedInternalResponse', type: true },
  //     { name: 'ExtractedRouteMethod', type: true },
  //   ],
  // })
}
