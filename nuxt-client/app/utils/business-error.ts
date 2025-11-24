import type { NuxtError } from '#app'

/**
 * 業務エラー。
 */
export interface BusinessError extends NuxtError {
  data: ErrorMessage[]
}

/**
 * エラーメッセージ。
 */
export interface ErrorMessage {
  code: string
  message: string
}

/**
 * 業務エラーである事を確認する型ガード。
 */
export const isBusinessError = (error: NuxtError): error is BusinessError => {
  return error.statusCode === 422
}
