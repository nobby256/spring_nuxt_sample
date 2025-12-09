import { useDataStore } from '../data-store'

export default definePreloadMiddleware(async (_to, _from) => {
  const _dataStore = useDataStore()
  // ==============================================================
  // 画面固有データのロード
  // ==============================================================
})
