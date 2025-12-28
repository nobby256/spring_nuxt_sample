import type { NuxtError } from '#app'

export const useNotification = () => {
  const toast = useToast()
  const notifyMessage = (message: string) => {
    toast.info({
      title: 'Info',
      message: message,
      timeout: 30000,
    })
  }
  const notifyError = (error: NuxtError) => {
    if (isDomainError(error)) {
      for (const message of error.data.messages ?? []) {
        toast.error({
          title: 'Error!',
          message: message.text,
          timeout: 30000,
        })
      }
    }
    else {
      toast.error({
        title: 'Error!',
        message: `status: ${error.statusCode}`,
        timeout: 30000,
      })
    }
  }

  return {
    notifyMessage,
    notifyError,
  }
}
