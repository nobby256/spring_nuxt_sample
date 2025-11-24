/*
 * NuxtAppがprivdeするコンポーネントを簡略化するための関数群。
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
 *
 * 以下がカスタマイズされている点です。
 * ・APIサーバーのベースURLをoptions.baseURLに設定する
 */
export const ofetch = async <T = unknown, R extends ResponseType = 'json'>(request: FetchRequest, options?: FetchOptions<R>): Promise<MappedResponseType<R, T>> => {
  const baseURL = useRuntimeConfig().public?.ofetch?.baseURL ?? ''
  return await useNuxtApp().$ofetch(request, { baseURL, ...options })
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
