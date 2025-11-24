// 検索条件
export type SearchCriteria = {
  name?: string
}

// アイテム
export type Item = {
  id: string
  name?: string
  price?: number
  maker?: string
  description?: string
}

// データストア
export const useDataStore = defineStore('$/crud/dataStore', {
  state: () => ({
    // 最後に使用した検索条件。undefinedは検索未実施を表す
    criteria: undefined as SearchCriteria | undefined,
    // アイテム一覧
    items: [] as Item[],
  }),
  actions: {
    // アイテム検索
    async search(searchCriteria: SearchCriteria): Promise<void> {
      this.items = await ofetch<Item[]>('/api/crud', {
        method: 'GET',
        query: {
          name: searchCriteria.name,
        },
      })
      this.criteria = searchCriteria
    },
    // アイテム取得
    async get(id: string): Promise<Item> {
      return await ofetch(`/api/crud/${id}`, {
        method: 'GET',
      })
    },
    // アイテム更新
    async update(item: Item): Promise<void> {
      await ofetch(`/api/crud/${item.id}`, {
        method: 'PUT',
        body: item,
      })
      // 再検索
      await this.search(this.criteria!)
    },
  },
})
