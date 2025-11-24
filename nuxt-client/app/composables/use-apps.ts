/*
 * NuxtAppがprivdeするコンポーネントを簡略化するための関数群。
 */
import type { NuxtApp, NuxtError } from '#app'

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

/**
 * 例外を正規化する関数。
 *
 * @param error 例外
 * @returns NuxtErrorに正規化された例外
 */
export const normalizeError = (error: unknown): NuxtError => {
  return useNuxtApp().$errorNormalizer.normalize(error)
}
