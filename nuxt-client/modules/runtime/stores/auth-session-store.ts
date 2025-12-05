// Removed unused import: RouteLocationNormalized

export const useAuthSessionStore = defineStore('$/global/AuthSession', {
  state: () => ({
    user: '',
    authorities: [] as string[],
    isAuthenticated: false,
  }),
  actions: {
    async fetch(): Promise<void> {
      const response = await $apifetch<{
        user: string
        authorities: string[]
        isAuthenticated: boolean
        csrfParameterToken: string
        csrfParameterName: string
      }
      >('/api/auth-session')

      this.user = response.user
      this.authorities = response.authorities
      this.isAuthenticated = response.isAuthenticated
    },
    hasAuthority(authority?: string): boolean {
      if (!authority) {
        return false
      }
      return this.authorities.includes(authority)
    },
    async logout(): Promise<void> {
      try {
        await $apifetch('/api/logout', { method: 'POST' })
      }
      catch (error) {
      // ログアウトでのエラーは無視する
        console.log(error)
      }
    },
  },
})
