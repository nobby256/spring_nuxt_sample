// @ts-check
import withNuxt from './.nuxt/eslint.config.mjs'

// 参考
// https://t-cr.jp/article/fgl1ir9uiehaqmm#google_vignette
// https://typescript-eslint.io/rules/
export default withNuxt({
  files: ['**/*.{ts,tsx,vue}'],
  languageOptions: {
    parserOptions: {
      project: [
        './.nuxt/tsconfig.app.json',
        './.nuxt/tsconfig.server.json',
        './.nuxt/tsconfig.shared.json',
        './.nuxt/tsconfig.node.json',
      ],
    },
  },
  rules: {
    '@typescript-eslint/no-floating-promises': 'error',
    '@typescript-eslint/prefer-nullish-coalescing': 'error',
    '@typescript-eslint/consistent-type-definitions': 'error',
  },
})
