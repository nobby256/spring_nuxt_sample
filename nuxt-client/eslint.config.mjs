// @ts-check
import withNuxt from './.nuxt/eslint.config.mjs'

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
  },
})
