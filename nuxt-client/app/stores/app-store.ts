import type { NuxtError } from '#app'

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
    profile: {
      user: undefined as string | undefined,
      username: undefined as string | undefined,
    },
  }),
  actions: {
    // アプリケーションの初期情報を取得する
    async load(): Promise<void> {
      try {
        const data = await apiFetch<InitialData>('/api/initial-data', { method: 'GET' })
        this.profile.user = data.usr
        this.profile.username = data.username
        // ロード完了
        this.loaded = true
      }
      catch (error) {
        // アプリケーションの初期情報の取得に失敗した場合は継続不能エラーとして扱う
        throw normalizeError(error, true)
      }
    },
    notifyMessage(message: string) {
      let messages = [] as string[]
      messages = ['【メッセージ】']
      messages.push(message)
      // サンプルなのでシンプルにalertで表示
      alert(messages.join('\n'))
    },
    notifyError(error: NuxtError) {
      let messages = [] as string[]
      if (isBusinessError(error)) {
        messages = ['【業務エラー】']
        messages.push(JSON.stringify(error.data))
      }
      else {
        messages = ['【その他のエラー】']
        messages.push(`status: ${error.statusCode}`)
      }
      // サンプルなのでシンプルにalertで表示
      alert(messages.join('\n'))
    },
  },
})
