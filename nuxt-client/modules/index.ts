import { addImportsDir, addImportsSources, addPlugin, addRouteMiddleware, createResolver, defineNuxtModule, type Resolver } from '@nuxt/kit'
import type { Nuxt } from '@nuxt/schema'
import { defu } from 'defu'
import { glob } from 'tinyglobby'
import { relative } from 'pathe'

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
  async setup(moduleOptions, nuxt) {
    nuxt.options.runtimeConfig.public.foundation = defu(
      nuxt.options.runtimeConfig.public.foundation ?? {},
      {
        ...moduleOptions,
      },
    )

    const resolver = createResolver(import.meta.url)
    addPlugins(resolver)
    addUtils(resolver)
    addStores(resolver)
    await addPreloadMiddleware(nuxt, resolver)
  },
})

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

async function addPreloadMiddleware(nuxt: Nuxt, resolver: Resolver) {
  // カレントディレクトリの絶対パス
  const absCurrentDir = resolver.resolve('.')
  // /app/pagesの絶対パス
  const absPagesDir = createResolver(nuxt.options.dir.app).resolve(nuxt.options.dir.pages)

  // CWD を app/pages にして glob（結果は pagesRoot からの相対パスで返す）
  const pattern: string = '**/*.preload-middleware.ts'
  const files = await glob(pattern, {
    cwd: absPagesDir,
    absolute: true,
    onlyFiles: true,
  })

  for (const abs of files) {
    // /app/pagesからの相対パスに変換
    const rel = relative(absPagesDir, abs)
    // rel 例: "foo/middleware/resolve.preload-middleware.ts"
    const normalized = rel.replace(/\\/g, '/')

    // middleware 名を決める
    // 例: "foo/middleware/resolve.preload-middleware.ts"
    //   → "foo-resolve.preload-middleware"
    const name = normalized
      .replace(/\.ts$/, '')
      .replace(/\/middleware\//, '-') // ".../middleware/xxx" → "...-xxx"
      .replace(/\//g, '-') // 残りの / を -

    // カレントからの相対パスを求める
    const resolvedPath = relative(absCurrentDir, abs)
    // 登録するパスは拡張子は不要
    const path = resolvedPath.replace(/\.ts$/, '')
    addRouteMiddleware({
      name,
      path,
      global: false,
    })

    if (nuxt.options.dev) {
      console.log(`[preload-middlewares] registered: ${name} -> ${resolvedPath}`)
    }
  }
}
