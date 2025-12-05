import { useAuthSessionStore } from '../stores/auth-session-store'

export default defineNuxtPlugin(async () => {
  await useAuthSessionStore().fetch()
})
