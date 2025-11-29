declare module '#app' {
  interface RuntimeNuxtHooks {
    /**
     * 継続可能なエラーが発生した事を伝えるフック。
     * @param error 継続可能なエラー。
     */
    'app:error:recoverable': (error: NuxtError) => void | Promise<void>
    'customize:fetch-options': (options: FetchOptions) => void
  }
}

export {}
