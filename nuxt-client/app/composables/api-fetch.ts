import type { NuxtApp } from '#app'

/**
 * useNuxtApp().$ofetchのラッパー関数。
 *
 * シグネチャはofetchと同じです。
 * 例：
 * const item = await apiFetch<Item>('/api/foo')
 * const item = await apiFetch<Item>('/api/bar', { method: 'POST', body: value })
 *
 * 以下がカスタマイズされている点です。
 * ・APIサーバーのベースURLをoptions.baseURLに設定する
 */
export const apiFetch: NuxtApp['$ofetch'] = async (request, opts?) => {
  const baseURL = useRuntimeConfig().public?.apiFetchBaseURL || ''
  return await useNuxtApp().$ofetch(request, { baseURL, ...opts })
}
