import type { NuxtError } from '#app'
import type { components } from '#open-fetch-schemas/backend'

export type DomainProblem = components['schemas']['DomainProblem']
export type DefaultDomainProblem = components['schemas']['DefaultDomainProblem']

/**
 * 業務エラー。
 */
export interface DomainError extends NuxtError {
  data: DomainProblem
}

/**
 * 業務エラーである事を確認する型ガード。
 */
export const isDomainError = (error: NuxtError): error is DomainError => {
  return error.statusCode === 422
}
