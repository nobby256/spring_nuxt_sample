// https://nuxt.com/docs/api/configuration/nuxt-config

// 開発モード判定
const isDev = process.env.NODE_ENV === 'development'

export default defineNuxtConfig({

  modules: [
    '@vee-validate/nuxt',
    '@pinia/nuxt',
    '@nuxt/eslint',
  ],

  ssr: false,

  runtimeConfig: {
    public: {
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

  foundation: {
    fetch: {
      baseURL: isDev ? 'http://localhost:8080' : '',
    },
  },
})
