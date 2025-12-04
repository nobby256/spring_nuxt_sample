export default defineNuxtRouteMiddleware(async (to, from) => {
  try {
    // to.matched 配列に含まれる全てのルートレコードを順番に処理する
    // これにより、親ルートの fetchData -> 子ルートの fetchData の順に実行される
    for (const routeRecord of to.matched) {
      const func = routeRecord.meta.resolveData
      if (func) {
        await func(to, from)
      }
    }
  }
  catch (error: unknown) {
    const nuxtError = normalizeError(error)
    // middlewareで発生した例外は強制的に継続不能扱いになってしまうため、!fatalの場合はabortNavigationする
    if (!nuxtError.fatal) {
      await useNuxtApp().callHook('app:error:recoverable', nuxtError)
      return abortNavigation()
    }
    throw nuxtError // 継続不能なエラーはそのままスローする
  }
})
