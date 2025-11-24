/**
 * エラーハンドラプラグイン。
 */
import type { NuxtError } from '#app'
import { showError } from '#app'

export default defineNuxtPlugin((nuxtApp) => {
  // グローバルエラーハンドラ
  // Vueコンポーネント内で発生した未キャッチ例外のhookです。
  // NuxtにおいてはvueApp.config.errorHandlerの代わりにこちらを使用します。
  //
  // 重要：
  // 非同期処理やwatchの中で発生した未キャッチ例外はvue.js/nuxt共にエラーハンドラで補足できません。
  // このようなケースでは明示的にエラーハンドラを呼び出す必要があります。
  // nuxtApp.callHook('vue:error', error)
  nuxtApp.hook('vue:error', async (error: unknown) => {
    const nuxtError = normalizeError(error)
    if (nuxtError.fatal) {
      showError(nuxtError)
    }
    else {
      await useNuxtApp().callHook('app:resolve-data-error', nuxtError)
    }
  })

  /**
   * error.vue遷移時のフック
  // このフックは以下の場合に呼び出されます：
  // 1. showError()が呼び出された時（showError内部で app:error フックを呼ぶ）
  // 2. Nuxtコンテキスト（Plugin、Middleware等）で未キャッチ例外が発生した時
  //    Nuxtコンテキストでの未キャッチ例外ではvue:errorは呼び出されずに直接showError()が呼ばれます
  // 3. callHook('app:error')を明示的に呼び出した時（通常行わない）
  //
  // 重要：
  // このフックが呼ばれている時点でerror.vueの表示は確定しています。
  // エラーページ表示を妨げる処理や、再度エラーページに遷移させる処理（showError()）は避けてください。
   */
  nuxtApp.hook('app:error', async (_error: NuxtError) => {
  })
})
