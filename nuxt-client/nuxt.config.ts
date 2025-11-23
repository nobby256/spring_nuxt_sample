// https://nuxt.com/docs/api/configuration/nuxt-config

// 開発モード判定
const isDev = process.env.NODE_ENV === 'development'

export default defineNuxtConfig({

  modules: [
    '@vee-validate/nuxt',
    '@pinia/nuxt',
    '@nuxt/eslint',
  ],

  // SpringBootと連携する為に必要
  ssr: false,

  router: {
    options: {
      // SpringBootと連携する為に必要（ssr=falseの時のみ有効な設定）
      hashMode: true,
    },
  },

  runtimeConfig: {
    public: {
      // サーバーのオリジン
      apiFetchBaseURL: isDev ? 'http://localhost:8080' : undefined,
    },
  },

  compatibilityDate: '2025-07-15',

  typescript: {
    typeCheck: true,
  },

  eslint: {
    config: {
      stylistic: true,
    },
  },
})
