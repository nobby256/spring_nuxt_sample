import type { NuxtError } from '#app'

export const normalizeError = (error: unknown): NuxtError => {
  let nuxtError: NuxtError
  if (isNuxtError(error)) {
    nuxtError = error
  }
  else if (error instanceof Error) {
    nuxtError = createError(error)
  }
  else {
    nuxtError = createError(String(error))
  }
  return nuxtError
}
