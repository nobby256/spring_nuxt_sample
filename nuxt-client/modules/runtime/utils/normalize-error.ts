import type { NuxtError } from '#app'
import { isNuxtError, createError } from '#app'

// --- 型定義 ---
export interface NormalizeErrorCallable {
  (error: unknown, options?: NormalizeErrorOptions): NuxtError
}

export interface NormalizeError extends NormalizeErrorCallable {
  create(options: NormalizeErrorOptions): NormalizeError
}

export interface NormalizeErrorOptions {
  fatal?: boolean
  rules?: NormalizeErrorRule[]
}

export interface NormalizeErrorRule {
  (error: NuxtError): boolean
}

// --- 実装 ---

/**
 * 2つのオプションオブジェクトをマージして、新しいオプションオブジェクトを返す。
 * 元のオブジェクトは変更されない（非破壊的）。
 * @param baseOptions - 土台となるオプション
 * @param overrideOptions - 上書きするためのオプション
 */
function mergeOptions(
  baseOptions: NormalizeErrorOptions,
  overrideOptions: NormalizeErrorOptions = {},
): NormalizeErrorOptions {
  // 1. baseOptionsのコピーを作成し、直接の変更（mutation）を防ぐ
  const merged: NormalizeErrorOptions = { ...baseOptions }

  // 2. 'fatal'プロパティをマージ
  // overrideOptionsで'fatal'が指定されていれば、その値で上書きする
  if (overrideOptions.fatal !== undefined) {
    merged.fatal = overrideOptions.fatal
  }

  // 3. 'rules'プロパティをマージ
  // overrideOptionsにrulesがあれば、既存のrules配列と結合（concat）して新しい配列を作成する
  if (overrideOptions.rules && overrideOptions.rules.length > 0) {
    const baseRules = baseOptions.rules ?? []
    merged.rules = [...baseRules, ...overrideOptions.rules]
  }

  return merged
}

function createInstance(instanceOptions: NormalizeErrorOptions): NormalizeError {
  // 1. 呼び出し可能なコア関数を定義
  const callable: NormalizeErrorCallable = (error: unknown, runtimeOptions?: NormalizeErrorOptions): NuxtError => {
    // 実行時のオプションマージ
    const finalOptions = mergeOptions(instanceOptions, runtimeOptions)

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

    if (finalOptions.fatal === true) {
      nuxtError.fatal = true
    }
    for (const rule of finalOptions.rules ?? []) {
      if (rule(nuxtError)) {
        break
      }
    }
    return nuxtError
  }

  // 2. 関数に`create`メソッドをアタッチ
  const instance = callable as NormalizeError
  instance.create = (newOptions: NormalizeErrorOptions): NormalizeError => {
    // 新しいインスタンスを作る際は、現在の設定と新しい設定をマージして渡す
    const mergedOptions = mergeOptions(instanceOptions, newOptions)
    return createInstance(mergedOptions)
  }

  return instance
}

// --- デフォルト設定とエクスポート ---

const defaultOptions: NormalizeErrorOptions = {
  rules: [
    (error: NuxtError): boolean => {
      const fatalStatusCodes = [401, 403]
      if (fatalStatusCodes.includes(error.statusCode)) {
        error.fatal = true
      }
      return false
    },
  ],
}

// デフォルトオプションで最初のインスタンスを生成
export const $normalizeError = createInstance(defaultOptions)
