// import type { ApiFetch } from '~/types/api-fetch'
import type { NuxtApp } from '#app'

type apiFetchCallable = NuxtApp['$apiFetch']

/**
 * useNuxtApp().$apiFetch()のラッパー関数。
 *
 * シグネチャは$apiFetchと同じなので使い方も同じです。
 * 例：
 * const item = await apiFetch<Item>('/api/foo')
 * const item = await apiFetch<Item>('/api/bar', { method: 'POST', body: value })
 *
 * $apiFetchをカスタマイズしている点
 * ・APIサーバーのベースURLをoptions.baseURLに設定する
 */
export const apiFetch: apiFetchCallable = async (request, options) => {
  const baseURL = useRuntimeConfig().public?.apiFetchBaseURL || ''
  return await useNuxtApp().$apiFetch(request, { baseURL, ...options })
}
