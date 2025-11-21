/**
 * エラーハンドラプラグイン。
 */
import type { NuxtError } from '#app'

export default defineNuxtPlugin((nuxtApp) => {
  // グローバルエラーハンドラ
  // Vueコンポーネント内で発生した未キャッチ例外のhookです。
  // NuxtにおいてはvueApp.config.errorHandlerの代わりにこちらを使用します。
  //
  // 重要：
  // 非同期処理やwatchの中で発生した未キャッチ例外はvue.js/nuxt共にエラーハンドラで補足できません。
  // このようなケースでは明示的にエラーハンドラを呼び出す必要があります。
  // nuxtApp.callHook('vue:error', error)
  nuxtApp.hook('vue:error', (error: unknown) => {
    // 業務エラーであるか否かを確認
    if (isBusinessError(error)) {
      const messages = ['【業務エラー】']
      for (const msg of error.data ?? []) {
        messages.push(`code: ${msg.code}\n${msg.message}`)
      }
      // サンプルなのでシンプルにalertで表示
      alert(messages.join('\n'))
      return
    }
    // 業務例外以外はerror.vueに切り替える
    showError(error instanceof Error ? error : new Error(String(error)))
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
  nuxtApp.hook('app:error', async (error: NuxtError) => {
    // エラー発生時には自動的にログアウトを行います
    // ただし、ログアウト呼び出しがエラーになっても無視します
    if (error.statusCode !== 401) {
      const appDataStore = useAppDataStore()
      await appDataStore.logout().catch((error) => {
        console.log(error)
      })
    }
  })
})
