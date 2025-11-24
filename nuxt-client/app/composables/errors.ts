import type { NuxtError } from '#app'

export const normalizeError = (error: unknown): NuxtError => {
  return useNuxtApp().$errorNormalizer.normalize(error)
}
