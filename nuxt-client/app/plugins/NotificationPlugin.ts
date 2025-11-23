import type { NuxtError } from '#app'

export default defineNuxtPlugin((nuxtApp) => {
  const notifyError = (error: NuxtError) => {
    let messages = [] as string[]
    if (isBusinessError(error)) {
      messages = ['【業務エラー】']
      for (const msg of error.data ?? []) {
        messages.push(`code: ${msg.code}\n${msg.message}`)
      }
    }
    else {
      messages = ['【その他のエラー】']
      messages.push(`status: ${error.statusCode}`)
    }
    // サンプルなのでシンプルにalertで表示
    alert(messages.join('\n'))
  }

  nuxtApp.hook('app:resolve-data-error', (error: NuxtError) => {
    notifyError(error)
  })
})
