import type { NuxtError } from '#app'

// 正規化ルールの型定義
type NormalizationRule = (error: NuxtError) => NuxtError | undefined

export const useErrorMormalizer = () => {
  const rules: NormalizationRule[] = [
    // デフォルトルールは、インスタンスのプライベートな状態
    (error) => {
      if (error.statusCode === 401 || error.statusCode === 403) {
        error.fatal = true
      }
      return undefined
    },
  ]

  const addNormalizationRule = (rule: NormalizationRule) => {
    rules.push(rule)
  }

  const normalize = (error: unknown): NuxtError => {
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

    // 登録されたルールを適用
    for (const rule of rules) {
      const result = rule(nuxtError)
      if (result) {
        nuxtError = result
      }
    }

    return nuxtError
  }

  return {
    normalize,
    addNormalizationRule,
  }
}
