export default defineNuxtRouteMiddleware(async (to, from) => {
  try {
    // to.matched 配列に含まれる全てのルートレコードを順番に処理する
    // これにより、親ルートの fetchData -> 子ルートの fetchData の順に実行される
    for (const routeRecord of to.matched) {
      if (routeRecord.meta.resolveData) {
        await routeRecord.meta.resolveData(to, from)
      }
    }
  }
  catch (error: unknown) {
    const nuxtError = normalizeError(error)
    if (nuxtError.fatal) {
      showError(nuxtError)
    }
    else {
      await useNuxtApp().callHook('app:resolve-data-error', nuxtError)
    }
    return abortNavigation()
  }
})
