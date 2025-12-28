import { useDataStore } from '../data-store'

export default definePreloadMiddleware(async (to, _from) => {
  const dataStore = useDataStore()
  // ==============================================================
  // 画面固有データのロード
  // ==============================================================
  const value = to.query.input as string
  await dataStore.send(value)
})
