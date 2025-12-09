export const useDataStore = defineStore('$/errorhandling/dataStore', {
  state: () => ({
    initialized: false,
    value: undefined as unknown as string,
  }),
  actions: {
    async initialize(): Promise<void> {
      this.initialized = true
    },
    async send(value: string): Promise<void> {
      const data = await apiFetch<{ message: string }>('/api/errorhandling', { method: 'POST', body: { value } })
      this.value = JSON.stringify(data)
    },
  },
})
