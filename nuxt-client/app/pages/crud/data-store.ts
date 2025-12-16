// 検索条件
export interface SearchCriteria {
  name?: string
}

// アイテム
export interface Item {
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
      this.items = await useNuxtApp().$backend('/api/crud', {
        query: {
          name: searchCriteria.name,
        },
      })
      this.criteria = searchCriteria
    },
    // アイテム取得
    async get(id: string): Promise<Item> {
      return await useNuxtApp().$backend('/api/crud/{id}', {
        path: { id },
      })
    },
    // アイテム更新
    async update(item: Item): Promise<void> {
      await useNuxtApp().$backend('/api/crud/{id}', {
        method: 'PUT',
        path: { id: item.id },
        body: item,
      })
      // 再検索
      await this.search(this.criteria!)
    },
  },
})
