export type WizForm = {
  input1: string
  input2: string
  input3: string
}

// データストア
export const useDataStore = defineStore('$/validation/wizard/dataStore', {
  state: () => ({
  }),
  actions: {
    async post(form: WizForm): Promise<void> {
      console.log(form)
    },
  },
})
