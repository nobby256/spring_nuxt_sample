import type { NuxtError } from '#app'

/**
 * 業務エラー。
 */
export type BusinessError = NuxtError<ErrorMessage[]>

/**
 * エラーメッセージ。
 */
export type ErrorMessage = {
  code: string
  message: string
}

/**
 * 業務エラーである事を確認する型ガード。
 */
export const isBusinessError = (error: unknown): error is BusinessError => {
  if (isNuxtError(error)) {
    if (error.statusCode === 422) {
      return true
    }
  }
  return false
}
