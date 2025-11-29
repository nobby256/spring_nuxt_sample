import type { NuxtError } from '#app'
import { isNuxtError, createError } from '#app'

// 正規化ルールの型定義
export interface NormalizationRule {
  (error: NuxtError): boolean
}

export interface NormalizeErrorContext {
  rules?: NormalizationRule[]
}

export interface NormalizeErrorSetupHook {
  (context: NormalizeErrorContext): void
}

export interface NormalizeError {
  normalize: (error: unknown, fatal: boolean | undefined) => NuxtError
  setup: (hook: NormalizeErrorSetupHook) => void
}

const rules: NormalizationRule[] = [
  (error) => {
    const fatalStatusCodes = [401, 403]
    if (fatalStatusCodes.includes(error.statusCode)) {
      error.fatal = true
    }
    return false
  },
]

export const useNormalizeError = (): NormalizeError => {
  const normalize = (error: unknown, fatal: boolean | undefined): NuxtError => {
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
    // ルールを適用
      for (const rule of rules) {
        if (rule(nuxtError)) {
          break
        }
      }
    }

    return nuxtError
  }

  const setup = (hook: NormalizeErrorSetupHook) => {
    const context: NormalizeErrorContext = {
      rules,
    }
    hook(context)
  }

  return {
    normalize,
    setup,
  }
}
