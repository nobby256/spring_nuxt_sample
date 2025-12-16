import type { NuxtError } from '#app'

export const useNotification = () => {
  const notifyMessage = (message: string) => {
    let messages = [] as string[]
    messages = ['【メッセージ】']
    messages.push(message)
    // サンプルなのでシンプルにalertで表示
    alert(messages.join('\n'))
  }
  const notifyError = (error: NuxtError) => {
    let messages = [] as string[]
    if (isDomainError(error)) {
      messages = ['【業務エラー】']
      if (error.data.type === '/domain-problem/message') {
        const errors = (error.data as DefaultDomainProblem).errors ?? []
        for (const err of errors) {
          messages.push(err.message)
        }
      }
      else {
        messages.push('えらいことが起きました')
      }
    }
    else {
      messages = ['【その他のエラー】']
      messages.push(`status: ${error.statusCode}`)
    }
    // サンプルなのでシンプルにalertで表示
    alert(messages.join('\n'))
  }

  return {
    notifyMessage,
    notifyError,
  }
}
