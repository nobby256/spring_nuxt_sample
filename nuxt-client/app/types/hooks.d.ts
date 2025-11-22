declare module '#app' {
  interface RuntimeNuxtHooks {
    /**
     * データ取得ミドルウェアで継続可能なエラーが発生した際に呼び出されるフック。
     * @param error 発生したエラーオブジェクト。
     */
    'app:resolve-data-error': (error: NuxtError) => void | Promise<void>

    /**
     * apiFetchで生成されたNuxtErrorオブジェクトをカスタマイズするためのフック。
     * 主にErrorをfatalにするルールを変更するために使用する。
     * 引数として渡されるNuxtErrorはHTTPステータスコードが401または403の場合はfatal=trueになっている。
     * @param error useApiFetch内で生成されたNuxtErrorオブジェクト。
     */
    'customize:api-error': (error: NuxtError) => void | Promise<void>
  }
}

export {}
