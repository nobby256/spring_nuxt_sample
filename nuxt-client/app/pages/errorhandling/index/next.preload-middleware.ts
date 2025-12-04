import { useDataStore } from '../data-store'

export default definePreloadMiddleware(async (to, _from) => {
  const value = to.query.input as string
  await useDataStore().send(value)
})
