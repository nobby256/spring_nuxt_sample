/**
 * スプラッシュスクリーンに遷移させるミドルウェア。
 */
export default defineNuxtRouteMiddleware((to) => {
  if (to.path === '/splash') {
    return
  }
  const appStore = useAppStore()
  if (!appStore.loaded) {
    return navigateTo({
      path: '/splash',
      query: {
        redirect: to.fullPath,
      },
    },
    { replace: true })
  }
})
