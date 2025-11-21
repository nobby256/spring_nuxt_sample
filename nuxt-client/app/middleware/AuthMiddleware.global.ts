import type { RouteLocationNormalized } from 'vue-router'

/**
 * 権限チェックを行うミドルウェア。
 */
export default defineNuxtRouteMiddleware((to: RouteLocationNormalized, _from: RouteLocationNormalized | undefined) => {
  const appDataStore = useAppDataStore()

  // AppDataStoreが読み込まれていない場合（初回ロード時）は、'/'以外なら'/'にリダイレクト。
  // '/'の場合は何もせずリターン（無限リダイレクト防止）。
  if (!appDataStore.loaded) {
    if (to.path === '/loading') {
      return
    }
    return navigateTo('/loading', { replace: true })
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

  const { authorities } = appDataStore

  // 以下のコードはコーディングの例であり、サンプルプロジェクトでは機能しません

  // サンプル：特定のパスに対する権限チェック
  if (to.path.startsWith('/admin/') && !authorities.includes('ADMIN')) {
    throw createError({
      statusCode: 403,
      statusMessage: 'Insufficient privileges for admin area',
    })
  }

  // サンプル：権限が全くない場合のチェック
  if (authorities.length === 0) {
    throw createError({
      statusCode: 403,
      statusMessage: 'No authorities assigned',
    })
  }
})
