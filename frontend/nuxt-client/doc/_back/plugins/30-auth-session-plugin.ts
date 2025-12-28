import { useAuthSessionStore } from '../../../modules/runtime/stores/auth-session-store'

export default defineNuxtPlugin(async () => {
  await useAuthSessionStore().fetch()
})
