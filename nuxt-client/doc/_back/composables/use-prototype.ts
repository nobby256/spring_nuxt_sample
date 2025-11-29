// ========================================================================
// 1. 型定義 (Type Definitions)
// ========================================================================
// このファイルで使われるすべての「形」を最初に定義する。
// exportするかどうかに関わらず、このセクションにすべてまとめる。

// --- Public Types ---
export interface BarOptions {
  value1?: string
  value2?: string
}

export interface Bar {
  doSomething(): string
}

// --- Private Types ---

// ========================================================================
// 2. 内部状態と定数 (Internal State & Constants)
// ========================================================================
const defaultOptions: Required<BarOptions> = {
  value1: 'hello',
  value2: 'world',
}

// ========================================================================
// 3. 公開API (Public API)
// ========================================================================
// --- Private Functions ---

// --- Public API ---
export const useBar = (options?: BarOptions): Bar => {
  const resolvedOptions: Required<BarOptions> = {
    ...defaultOptions,
    ...(options ?? {}),
  }

  const doSomething = (): string => {
    return (resolvedOptions.value1 ?? '') + (resolvedOptions.value2 ?? '')
  }

  return {
    doSomething,
  }
}
