/**
 * アプリケーション全体で使用する共通データストア。
 */

type InitialData = {
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
      const initialData = await ofetch<InitialData>('/api/initial-data', { method: 'GET' })
      this.username = initialData.username
      this.authorities = initialData.authorities
      // ロード完了
      this.loaded = true
    },

    // ログアウト
    async logout(): Promise<void> {
      await ofetch('/api/logout', { method: 'POST' })
    },
  },
})
