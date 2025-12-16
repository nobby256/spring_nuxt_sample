// https://nuxt.com/docs/api/configuration/nuxt-config

// 開発モード判定
const isDev = process.env.NODE_ENV === 'development'

export default defineNuxtConfig({

  modules: [
    '@vee-validate/nuxt',
    '@pinia/nuxt',
    '@nuxt/eslint',
    'nuxt-open-fetch',
  ],
  ssr: false,

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

  // runtimeConfig: {
  //   public: {
  //     fetch: {
  //       baseURL: isDev ? 'http://localhost:8080' : '',
  //     },
  //   },
  // },

  openFetch: {
    clients: {
      backend: {
        baseURL: isDev ? 'http://localhost:8080' : '',
        schema: 'http://localhost:8080/v3/api-docs',
      },
    },
  },
})
