// ========================================================================
// 1. 型定義 (Type Definitions)
// ========================================================================
// このファイルで使われるすべての「形」を最初に定義する。
// exportするかどうかに関わらず、このセクションにすべてまとめる。

// --- Public Types ---
// モジュールの外から使われることが想定される型
export interface BarOptions {
  value1?: string
  value2?: string
}

export interface Bar {
  doSomething(): string
}

// --- Private Types ---
// このモジュールの内部実装でのみ使用される型
// (例えば、公開するSingletonよりも多くのプロパティを持つ内部的なインスタンスの型)
interface InternalBar extends Bar {
  _resolvedOptions: Required<BarOptions> // 内部的に保持する設定値
}

// ========================================================================
// 2. 内部状態と定数 (Internal State & Constants)
// ========================================================================
const defaultOptions: Required<BarOptions> = {
  value1: 'hello',
  value2: 'world',
}

// インスタンス変数の型注釈には、内部用の`InternalSingleton`を使用できる
let instance: InternalBar | undefined

// ========================================================================
// 3. 公開API (Public API)
// ========================================================================
export const setupUseBar = (options?: BarOptions): Bar => { // 戻り値は公開用の`Bar`
  if (instance) {
    throw new Error('Bar has already been created. `setupUseBar` should only be called once.')
  }

  const resolvedOptions: Required<BarOptions> = {
    ...defaultOptions,
    ...(options ?? {}),
  }

  const doSomething = (): string => {
    return (resolvedOptions.value1 ?? '') + (resolvedOptions.value2 ?? '')
  }

  // 実際のインスタンスは`InternalSingleton`の形で作られる
  instance = {
    doSomething,
    _resolvedOptions: resolvedOptions,
  }

  // ただし、戻り値として返す際は、公開用の`Singleton`として扱われるため、
  // 内部プロパティ(_resolvedOptions)は外部からは見えない。
  return instance
}

export const useBar = (): Bar => {
  if (!instance) {
    throw new Error('Bar has not been created yet. Call `setuoUseBar` in a Nuxt plugin first.')
  }
  return instance
}
