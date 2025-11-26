export const useAuthSessionStore = defineStore('$/global/AuthSession', {
  state: () => ({
    user: '',
    authorities: [] as string[],
    isAuthenticated: false,
  }),
  actions: {
    async logout(): Promise<void> {
      try {
        await useNuxtApp().$ofetch('/api/logout', { method: 'POST' })
      }
      catch (error) {
      // ログアウトでのエラーは無視する
        console.log(error)
      }
    },
  },
})
