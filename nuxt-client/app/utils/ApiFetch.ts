import type { ApiFetch } from '~/types/api-fetch'

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
export const apiFetch: ApiFetch = async (request, options) => {
  // サーバーのベースURLをruntimeConfigから取得する
  const baseURL = useRuntimeConfig().public?.serverOrigin || ''
  return await useNuxtApp().$apiFetch(request, { baseURL, ...options })
}
