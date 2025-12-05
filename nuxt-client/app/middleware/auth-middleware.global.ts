/**
 * 権限チェックを行うミドルウェア。
 * 画面を利用する為に権限が必要なアプリの場合は権限チェックをここで実装します。
 *
 * 通常、遷移を行うリンクやボタンはディセーブルになっているはずです。
 * そのため、通常の利用の範疇では権限がない画面に移動することはありません。
 * しかしブラウザのアドレスバーに遷移先画面のURLを手入力すれば直接移動できてしまいます。
 * そのような行為をガードする為にここでチェックする必要があります。
 */
export default defineNuxtRouteMiddleware((to) => {
  if (to.name === 'error') {
    return
  }

  const { hasAuthority } = useAuthSessionStore()
  if (!hasAuthority(to.meta.authority)) {
    throw createError({ statusCode: 403, statusMessage: 'UNAUTHORIZED' })
  }
})
