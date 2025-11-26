/*
 * NuxtAppがprivdeするコンポーネント利用を簡略化するための関数群。
 */
import type { ResponseType, FetchRequest, FetchOptions, MappedResponseType } from 'ofetch'
import type { NitroFetchRequest, NitroFetchOptions } from 'nitropack'

/**
 * useNuxtApp().$ofetchのラッパー関数。
 *
 * シグネチャはofetchと同じです。
 * 例：
 * const item = await ofetch<Item>('/api/foo')
 * const item = await ofetch<Item>('/api/bar', { method: 'POST', body: value })
 */
export const ofetch = async <T = unknown, R extends ResponseType = 'json'>(request: FetchRequest, options?: FetchOptions<R>): Promise<MappedResponseType<R, T>> => {
  return await useNuxtApp().$ofetch(request, options)
}
export const apiFetch = async <T = unknown>(url: NitroFetchRequest, options?: NitroFetchOptions<NitroFetchRequest>): Promise<T> => {
  return await useNuxtApp().$apifetch(url, options)
}
