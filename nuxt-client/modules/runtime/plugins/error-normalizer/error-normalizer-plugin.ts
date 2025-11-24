import { useErrorMormalizer } from './use-error-normalizer'

export default defineNuxtPlugin((_nuxtApp) => {
  const errorNormalizer = useErrorMormalizer()
  return {
    provide: {
      // useNuxtApp().$errorNormalizer
      errorNormalizer,
    },
  }
})
