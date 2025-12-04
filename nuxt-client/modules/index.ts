import type { Nuxt, NuxtApp } from '@nuxt/schema'
import { addImportsDir, addImportsSources, addPlugin, addRouteMiddleware, createResolver, defineNuxtModule, type Resolver } from '@nuxt/kit'
import { defu } from 'defu'
import { join, relative } from 'pathe'
import { glob } from 'tinyglobby'

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
    addMiddlewares(nuxt, resolver)
    addPlugins(resolver)
    addUtils(resolver)
    addStores(resolver)
    await addAppRouteMiddleware(nuxt, resolver)
  },
})

function addMiddlewares(nuxt: Nuxt, resolver: Resolver) {
  addRouteMiddleware({
    name: '10-authentication-middleware',
    path: resolver.resolve('./runtime/middleware/10-authentication-middleware.global'),
    global: true,
  })
  addRouteMiddleware({
    name: 'resolve-data-middleware',
    path: resolver.resolve('./runtime/middleware/resolve-data-middleware.global'),
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

async function addAppRouteMiddleware(nuxt: Nuxt, resolver: Resolver) {
  const rootResolver = createResolver(nuxt.options.rootDir)
  const appResolver = createResolver(nuxt.options.dir.app)
  const pagesRoot = appResolver.resolve(nuxt.options.dir.pages) // /app/pagesの絶対パス
  const appDir = '/' + relative(nuxt.options.rootDir, nuxt.options.dir.app) // /app
  const pagesDir = `${appDir}/${nuxt.options.dir.pages}` // /app/pages

  // CWD を app/pages にして glob（結果は pagesRoot からの相対パスで返す）
  const pattern: string = '**/*-middleware.ts'
  const files = await glob(pattern, {
    cwd: pagesRoot,
    absolute: false,
    onlyFiles: true,
  })

  for (const rel of files) {
    // rel 例: "foo/middleware/resolve-data-middleware.ts"
    const normalized = rel.replace(/\\/g, '/')

    // middleware 名を決める
    // 例: "foo/middleware/resolve-data-middleware.ts"
    //   → "foo-resolve-data-middleware"
    const name = normalized
      .replace(/\.ts$/, '')
      .replace(/\/middleware\//, '-') // ".../middleware/xxx" → "...-xxx"
      .replace(/\//g, '-') // 残りの / を -

    // Nuxt から見えるパス（rootDir 基準）に直す
    const resolvedPath = resolver.resolve(pagesDir, rel)
    // このファイルの場所からの相対パス（rootDir 基準）に直す
    const relativePath = './..' + resolvedPath
    // 登録するパスは拡張子は不要
    const path = relativePath.replace(/\.ts$/, '')
    addRouteMiddleware({
      name,
      path,
      global: false,
    })

    if (nuxt.options.dev) {
      console.log(`[page-middlewares] registered: ${name} -> ${resolvedPath}`)
    }
  }
}
