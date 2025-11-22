import type { $Fetch, NitroFetchOptions, NitroFetchRequest } from 'nitropack'
import { normalizeError } from './Errors'

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
    return await useNuxtApp().$apiFetch(url, { baseURL, ...options })
  }
  catch (error) {
    // 発生したエラーをNuxtErrorに正規化し、401/403エラーの場合にfatalエラーとする
    const nuxtError = normalizeError(error)
    if (nuxtError.statusCode === 401 || nuxtError.statusCode === 403) {
      nuxtError.fatal = true
    }
    // フックを呼び出すことでErrorをカスタマイズ可能にする
    // 例：fatalエラーにするルールを変更する等
    await useNuxtApp().callHook('customize:api-error', nuxtError)
    throw nuxtError
  }
}
