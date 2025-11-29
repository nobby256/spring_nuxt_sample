export const useAuthSessionStore = defineStore('$/global/AuthSession', {
  state: () => ({
    user: '',
    authorities: [] as string[],
  }),
  actions: {
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
