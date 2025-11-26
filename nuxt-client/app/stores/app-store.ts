/**
 * アプリケーション全体で使用する共通データストア。
 */

interface InitialData {
  usr: string
  username: string
}

export const useAppStore = defineStore('$/global/application', {
  state: () => ({
    loaded: false,
    user: undefined as string | undefined,
    username: undefined as string | undefined,
  }),
  actions: {
    // アプリケーションの初期情報を取得する
    async load(): Promise<void> {
      try {
        const data = await ofetch<InitialData>('/api/initial-data', { method: 'GET' })
        this.user = data.usr
        this.username = data.username
        // ロード完了
        this.loaded = true
      }
      catch (error) {
        const nuxtError = useNormalizeError().normalize(error)
        // アプリケーションの初期情報の取得に失敗した場合は継続不能エラーとして扱う
        nuxtError.fatal = true
        throw nuxtError
      }
    },
  },
})
