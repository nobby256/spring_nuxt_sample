export const useDataStore = defineStore('$/errorhandling/dataStore', {
  actions: {
    async send(value: string) {
      return apiFetch<{ message: string }>('/api/errorhandling', { method: 'POST', body: { value } })
    },
  },
})
