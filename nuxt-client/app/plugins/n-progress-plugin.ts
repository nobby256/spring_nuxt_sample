import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

export default defineNuxtPlugin((nuxtApp) => {
  nuxtApp.hook('page:loading:start', () => {
    if (!NProgress.isStarted()) {
      NProgress.start()
    }
  })
  nuxtApp.hook('page:loading:end', progressDone)
  nuxtApp.hook('app:error', progressDone)
})

const progressDone = async () => {
  await nextTick()
  NProgress.done()
}
