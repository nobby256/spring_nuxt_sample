import { readdirSync } from 'fs'
import { defineNuxtModule, addPlugin, createResolver, addImportsDir, type Resolver } from '@nuxt/kit'

// モジュールオプションの型定義
export interface ModuleOptions {
  bff?: {
    baseURL?: string
    csrfURL?: string
    // scfw-ssoのloginUrlと同じ値を指定します。デフォルトは`/login`です。
    loginURL?: string
    // scfw-ssoのlogoutUrlと同じ値を指定します。デフォルトは`/login`です。
    logoutURL?: string
    csrfHeaderName?: string
    csrfCookieName?: string
  }
}

// ランタイムコンフィグの型定義
export interface ModuleRuntimeConfig {
  bff: {
    baseURL: string
    csrfURL: string
    loginURL: string
    logoutURL: string
    csrfHeaderName: string
    csrfCookieName: string
  }
}

// デフォルト値
const DEFAULT_BASE_URL = ''
const DEFAULT_CSRF_URL = '/csrf'
const DEFAULT_LOGIN_URL = '/login'
const DEFAULT_LOGOUT_URL = '/logout'
const DEFAULT_CSRF_HEADER_NAME = 'X-XSRF-TOKEN'
const DEFAULT_CSRF_COOKIE_NAME = 'XSRF-TOKEN'

// モジュール定義
export default defineNuxtModule<ModuleOptions>({
  meta: {
    name: 'scfw-spa-nuxt',
    configKey: 'scfw',
    compatibility: {
      nuxt: '^3.0.0',
    },
  },
  defaults: {
    bff: {
      baseURL: DEFAULT_BASE_URL,
      csrfURL: DEFAULT_CSRF_URL,
      loginURL: DEFAULT_LOGIN_URL,
      logoutURL: DEFAULT_LOGOUT_URL,
      csrfHeaderName: DEFAULT_CSRF_HEADER_NAME,
      csrfCookieName: DEFAULT_CSRF_COOKIE_NAME,
    },
  },
  setup(moduleOptions, nuxt) {
    const resolver = createResolver(import.meta.url)

    // =================================
    // ランタイムコンフィグの設定
    // =================================
    const baseURL = moduleOptions?.bff?.baseURL || DEFAULT_BASE_URL
    const config: ModuleRuntimeConfig = {
      bff: {
        // BFFのコンテキストパス
        baseURL: baseURL,
        // fetchで指定するURLなのでbaseURLは不要（fetchのoptionsでbaseURLは指定済み）
        csrfURL: moduleOptions?.bff?.csrfURL || DEFAULT_CSRF_URL,
        // ブラウザのlocationを切り替える移動方式なのでbaseURL込みのURLになる
        loginURL: baseURL + moduleOptions?.bff?.loginURL || DEFAULT_LOGIN_URL,
        logoutURL: baseURL + moduleOptions?.bff?.logoutURL || DEFAULT_LOGOUT_URL,
        // クッキーの設定（デフォルトはSpringSecurityと同じ）
        csrfHeaderName: moduleOptions.bff?.csrfHeaderName || DEFAULT_CSRF_HEADER_NAME,
        csrfCookieName: moduleOptions.bff?.csrfCookieName || DEFAULT_CSRF_COOKIE_NAME,
      },
    }
    nuxt.options.runtimeConfig.public = nuxt.options.runtimeConfig.public || {}
    nuxt.options.runtimeConfig.public.scfw = config

    // =================================
    // ミドルウェアの登録
    // =================================
    // addMiddlewares(resolver)

    // =================================
    // プラグインの登録
    // =================================
    addPlugins(resolver)

    // =================================
    // コンポーザブルの登録
    // =================================
    addComposables(resolver)

    // =================================
    // ユーティリティを自動インポートとして登録
    // =================================
    addUtils(resolver)
  },
})

// function addMiddlewares(resolver: Resolver) {
//   addRouteMiddleware({
//     name: 'endureCsrfTokenMiddleware',
//     path: resolver.resolve('./runtime/middlewares/EnsureCsrftokenMiddleware.global'),
//     global: true,
//   })
// }

function getJsModuleNames(targetDir: string): string[] {
  const jsModuleNames = readdirSync(targetDir)
    .filter((file: string) => file.endsWith('.ts') && !file.endsWith('.d.ts'))
    .map((file: string) => file.replace('.ts', ''))
  return jsModuleNames
}

function addPlugins(resolver: Resolver) {
  // プラグインディレクトリ内のすべての.tsファイルを自動で登録
  const jsModuleNames = getJsModuleNames(resolver.resolve('./runtime/plugins'))
  jsModuleNames.forEach((pluginName: string) => {
    addPlugin(resolver.resolve(`./runtime/plugins/${pluginName}`))
  })

  // モジュールオプションを利用する等して個別に追加する場合
  // addPlugin(resolver.resolve('./runtime/plugins/ErrorHandlingPlugin'))
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
