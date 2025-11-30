import type { RouteLocationNormalized } from 'vue-router'

/**
 * 権限チェックを行うミドルウェア。
 */
export default defineNuxtRouteMiddleware((to: RouteLocationNormalized, _from: RouteLocationNormalized | undefined) => {
  if (to.path === '/error') {
    return
  }

  // 画面を利用する為に権限が必要なアプリの場合は権限チェックをここで実装します。
  //
  // 【セキュリティの多層防御】
  // 通常、遷移を行うリンクやボタンはディセーブルになっているはずです。
  // そのため、通常の利用の範疇では権限がない画面に移動することはありません。
  // しかしブラウザのアドレスバーに遷移先画面のURLを手入力すれば直接移動できてしまいます。
  // そのような行為をガードする為にここでチェックする必要があります。
  //
  // そもそも正常利用を想定したチェックではない為、エラーを検出した場合は例外をスローしエラー画面に遷移させます。
  const { authorities } = useAuthSessionStore()
  const authority = to.meta.authority
  if (authority && !authorities.includes(authority)) {
    throw createError({ statusCode: 403, statusMessage: '権限がありません。' })
  }
})
