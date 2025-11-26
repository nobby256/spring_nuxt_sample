import { createNormalizeError } from './create-normalize-error'

export default defineNuxtPlugin(() => {
  const normalizeError = createNormalizeError()
  return {
    provide: {
      // useNuxtApp().$normalizeError
      normalizeError,
    },
  }
})
