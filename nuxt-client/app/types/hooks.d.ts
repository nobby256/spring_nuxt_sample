declare module '#app' {
  interface RuntimeNuxtHooks {
    /**
     * データ取得ミドルウェアで継続可能なエラーが発生した際に呼び出されるフック。
     * @param error 発生したエラーオブジェクト。
     */
    'app:resolve-data-error': (error: NuxtError) => void | Promise<void>
  }
}

export {}
