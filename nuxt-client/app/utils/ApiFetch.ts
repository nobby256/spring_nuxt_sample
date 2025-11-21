import type { $Fetch, NitroFetchOptions, NitroFetchRequest } from '~/plugins/ApiFetchPlugin'

/**
 * カスタマイズした$fetch関数。
 *
 * シグネチャは$fetchと同じなので使い方も同じです。
 * 例：
 * apiFetch('/api/foo')
 * apiFetch('/api/bar', { method: 'POST', body: value })
 *
 * $fetchをカスタマイズされている点
 * ・ベースURLを設定する
 * ・CSRFトークンクッキーをリクエストヘッダーに追加する
 * ・Acceptにapplication/jsonを追加する
 * ・ブラウザのクッキーを有効にする
 * ・例外をNuxtErrorとしてスローする
 */
export async function apiFetch<T>(
  url: string,
  options?: NitroFetchOptions<NitroFetchRequest>,
): Promise<T> {
  // サーバーのベースURLをruntimeConfigから取得する
  const baseURL = useRuntimeConfig().public?.serverOrigin || ''
  try {
    const $apiFetch = useNuxtApp()['$apiFetch'] as $Fetch
    return await $apiFetch<T>(url, { baseURL, ...options })
  }
  catch (error) {
    // 発生したエラーをNuxtErrorに正規化する
    throw createError(error instanceof Error ? error : new Error(String(error)))
  }
}
