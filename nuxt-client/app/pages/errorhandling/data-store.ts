export const useDataStore = defineStore('$/errorhandling/dataStore', {
  actions: {
    async send(value: string) {
      return ofetch<{ message: string }>('/api/errorhandling', { method: 'POST', body: { value } })
    },
  },
})
