/*
 * NuxtAppがprivdeするコンポーネント利用を簡略化するための関数群。
 */
import type { NuxtError } from '#app'
import type { ResponseType, FetchRequest, FetchOptions, MappedResponseType } from 'ofetch'

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

/**
 * useNuxtApp().$errorNormalizer.normalizeのラッパー関数。
 * 例外を正規化する関数。
 *
 * @param error 例外
 * @returns NuxtErrorに正規化された例外
 */
export const normalizeError = (error: unknown): NuxtError => {
  return useNuxtApp().$errorNormalizer.normalize(error)
}
