export default defineNuxtRouteMiddleware(async (to) => {
  try {
    // to.matched 配列に含まれる全てのルートレコードを順番に処理する
    // これにより、親ルートの fetchData -> 子ルートの fetchData の順に実行される
    for (const routeRecord of to.matched) {
      if (routeRecord.meta.fetchData) {
        await routeRecord.meta.fetchData(to)
      }
    }
  }
  catch (error: unknown) {
    if (isNuxtError(error)) {
      // 認証/認可系のエラーは継続不能エラー扱いとする
      if (error.statusCode === 401 || error.statusCode === 403) {
        throw error
      }

      // それ以外のエラーはメッセージを表示してアプリを継続
      const notificationStore = useNotificationStore()
      notificationStore.showError(error)
    }
    return abortNavigation()
  }
})
