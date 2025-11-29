import { useAuthSessionStore } from '../stores/auth-session-store'

interface AuthSession {
  user: string
  authorities: string[]
}

export default defineNuxtPlugin(async () => {
  const data = await $apifetch<AuthSession>('/api/auth-session')

  const store = useAuthSessionStore()
  store.user = data.user
  store.authorities = data.authorities
})
