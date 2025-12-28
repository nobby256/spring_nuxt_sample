// Removed unused import: RouteLocationNormalized

export const useAuthSessionStore = defineStore('$/global/AuthSession', {
  state: () => ({
    user: '',
    authorities: [] as string[],
    authenticated: false,
  }),
  actions: {
    async fetch(): Promise<void> {
      try {
        const response = await useNuxtApp().$backend('/api/auth-session')

        this.user = response.user!
        this.authorities = response.authorities!
        this.authenticated = response.authenticated!
      }
      catch (error) {
        const nuxtError = normalizeError(error)
        nuxtError.fatal = true // ユーザー情報取得でのエラーは継続不能エラーとする
        throw nuxtError
      }
    },
    hasAuthority(authority?: string): boolean {
      if (!authority) {
        return true
      }
      return this.authorities.includes(authority)
    },
    async logout(): Promise<void> {
      try {
        await useNuxtApp().$backend('/api/logout')
      }
      catch (error) {
      // ログアウトでのエラーは無視する
        console.log(error)
      }
    },
  },
})
