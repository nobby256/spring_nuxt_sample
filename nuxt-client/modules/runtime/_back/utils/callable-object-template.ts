import { defu } from 'defu'
import { createCallableObjectProxy } from './callable-object-proxy'

// ========================================================================
// 型定義 (Type Definitions)
// ========================================================================

// --- Public Types ---

/**
 * オプション。
 */
export interface MyFuncOptions {
  value1?: string
  value2?: string
}

/**
 * 関数呼び出し形式のシグネチャ。
 */
export interface MyFuncCallable {
  (arg: string, options?: MyFuncOptions): string
}

/**
 * 外部に公開される型。
 */
export interface MyFunc extends MyFuncCallable {
  create(options: MyFuncOptions): MyFunc
}

// --- Private Types ---
// (必要に応じて内部的な型をここに定義)

// ========================================================================
// 内部状態と定数 (Internal State & Constants)
// ========================================================================

/**
 * $myFunc シングルトンが使用するデフォルトのオプション。
 */
const defaultOptions: MyFuncOptions = {
  value1: 'hello',
  value2: 'world',
}

/**
 * アプリケーション全体で共有されるシングルトンインスタンス。
 * 初期状態ではデフォルト設定で初期化される。
 * プラグインによって、あとから上書きされる可能性がある。
 */
let singletonInstance: MyFunc = createInstance(defaultOptions)

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
  baseOptions: MyFuncOptions,
  overrideOptions: MyFuncOptions = {},
): MyFuncOptions {
  // defuは第一引数のプロパティを優先するため、overrideOptionsを先に渡す
  return defu(overrideOptions, baseOptions)
  // defuを使わない場合は、baseOptionsのコピーを作成し、直接の変更を防ぐ
  // const merged: MyFuncOptions = { ...baseOptions }
  // カスタムでマージする
  // return merged
}

/**
 * 新しいインスタンスを生成する。
 * @param instanceOptions オプション
 */
function createInstance(instanceOptions: MyFuncOptions): MyFunc {
  // このインスタンスの本体となる、呼び出し可能な関数を定義
  const callable: MyFuncCallable = (arg: string, runtimeOptions?: MyFuncOptions): string => {
    // 実行時に渡されたオプションをマージ
    const finalOptions = mergeOptions(instanceOptions, runtimeOptions)

    // --- ここにこのヘルパーの主となる処理を記述 ---
    // (argも本来はこの処理で使用する)
    return `arg is "${arg}", result is "${finalOptions.value1} ${finalOptions.value2}"`
  }

  // 作成した関数に`.create`メソッドをアタッチ
  const instance = callable as MyFunc
  instance.create = (newOptions: MyFuncOptions): MyFunc => {
    // 現在のインスタンスの設定をベースに、新しい設定をマージ
    const mergedOptions = mergeOptions(instanceOptions, newOptions)
    // マージした設定で、新しいインスタンスを再帰的に生成
    return createInstance(mergedOptions)
  }

  return instance
}

// ========================================================================
// 外部公開API (Public API)
// ========================================================================

/**
 * MyFuncのシングルトン。
 */
export const $myFunc: MyFunc = createCallableObjectProxy(() => singletonInstance)

/**
 * シングルトンインスタンスを外部から差し替えるための関数。
 * 主にプラグインやテストコードでの使用を想定。
 * アンダースコアは「内部的な操作」であることを示唆する。
 * @param instance 差し替えるインスタンス
 */
export function _replace$normalizeError(instance: MyFunc): void {
  singletonInstance = instance
}
