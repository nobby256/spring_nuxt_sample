import type { NuxtError } from '#app'
import { isNuxtError, createError } from '#app'
import { createCallableObjectProxy } from './callable-object-proxy'

// ========================================================================
// 型定義 (Type Definitions)
// ========================================================================

// --- Public Types ---

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

// --- Private Types ---
// (必要に応じて内部的な型をここに定義)

// ========================================================================
// 内部状態と定数 (Internal State & Constants)
// ========================================================================

/**
 * デフォルトのオプション。
 */
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

/**
 * アプリケーション全体で共有されるシングルトンインスタンス。
 * 初期状態ではデフォルト設定で初期化される。
 * プラグインによって、あとから上書きされる可能性がある。
 */
let singletonInstance: NormalizeError = createInstance(defaultOptions)

// ========================================================================
// 内部関数 (Internal Functions)
// ========================================================================

/**
 * 2つのオプションオブジェクトをマージして、新しいオプションオブジェクトを返す。
 * defuを使用し、overrideOptionsのプロパティがbaseOptionsを上書きする。
 * @param baseOptions 土台となるオプション
 * @param overrideOptions 上書きするためのオプション
 */
function mergeOptions(
  baseOptions: NormalizeErrorOptions,
  overrideOptions: NormalizeErrorOptions = {},
): NormalizeErrorOptions {
  // baseOptionsのコピーを作成し、直接の変更を防ぐ
  const merged: NormalizeErrorOptions = { ...baseOptions }

  // 'fatal'プロパティをマージ
  if (overrideOptions.fatal !== undefined) {
    merged.fatal = overrideOptions.fatal
  }

  // 'rules'プロパティをマージ
  if (overrideOptions.rules && overrideOptions.rules.length > 0) {
    const baseRules = baseOptions.rules ?? []
    merged.rules = [...baseRules, ...overrideOptions.rules]
  }

  return merged
}

/**
 * 新しいインスタンスを生成する。
 * @param instanceOptions オプション
 */
function createInstance(instanceOptions: NormalizeErrorOptions): NormalizeError {
  // Callable関数を定義
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

  // Callable関数に`create`メソッドをアタッチ
  const instance = callable as NormalizeError
  instance.create = (newOptions: NormalizeErrorOptions): NormalizeError => {
    // 新しいインスタンスを作る際は、現在の設定と新しい設定をマージして渡す
    const mergedOptions = mergeOptions(instanceOptions, newOptions)
    return createInstance(mergedOptions)
  }

  return instance
}

// ========================================================================
// 外部公開API (Public API)
// ========================================================================

/**
 * NormalizeErrorのシングルトン。
 */
export const $normalizeError2: NormalizeError = createCallableObjectProxy(() => singletonInstance)

/**
 * シングルトンインスタンスを外部から差し替えるための関数。
 * 主にプラグインやテストコードでの使用を想定。
 * アンダースコアは「内部的な操作」であることを示唆する。
 * @param instance 差し替えるインスタンス
 */
export function _replace$normalizeError(instance: NormalizeError): void {
  singletonInstance = instance
}
