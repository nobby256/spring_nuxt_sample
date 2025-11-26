/**
 * アプリケーション全体で使用する共通データストア。
 */

interface InitialData {
  username: string
  authorities: string[]
}

export const useAuthenticationStore = defineStore('$/global/Authentication', {
  state: () => ({
    loaded: false,
    username: '' as string,
    authorities: [] as string[],
  }),
  actions: {
    // アプリケーションの初期情報を取得する
    async load(): Promise<void> {
      try {
        const initialData = await ofetch<InitialData>('/api/initial-data', { method: 'GET' })
        this.username = initialData.username
        this.authorities = initialData.authorities
        // ロード完了
        this.loaded = true
      }
      catch (error) {
        const normalizedError = normalizeError(error)
        // アプリケーションの初期情報の取得に失敗した場合は致命的エラーとして扱う
        normalizedError.fatal = true
        throw normalizedError
      }
    },

    // ログアウト
    async logout(): Promise<void> {
      try {
        await ofetch('/api/logout', { method: 'POST' })
      }
      catch (error) {
      // ログアウトでのエラーは無視する
        console.log(error)
      }
    },
  },
})
