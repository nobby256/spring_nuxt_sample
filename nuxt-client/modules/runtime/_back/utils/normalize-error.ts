import type { NuxtError } from '#app'
import { isNuxtError, createError } from '#app'

export const normalizeError = (error: unknown, fatal: boolean | undefined): NuxtError => {
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

  if (fatal) {
    nuxtError.fatal = true
  }
  else {
    const faitalStatus = [401, 403]
    if (faitalStatus.includes(nuxtError.statusCode)) {
      nuxtError.fatal = true
    }
  }

  return nuxtError
}
