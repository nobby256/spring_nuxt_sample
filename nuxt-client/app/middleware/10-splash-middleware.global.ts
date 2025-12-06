/**
 * スプラッシュスクリーンに遷移させるミドルウェア。
 */
export default defineNuxtRouteMiddleware(() => {
  const appStore = useAppStore()
  if (!appStore.loaded) {
    return navigateTo('/splash', { replace: true })
  }
})
