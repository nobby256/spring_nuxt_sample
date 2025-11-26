import { useAuthSessionStore } from '../../stores/auth-session-store'

interface AuthSession {
  user: string
  authorities: string[]
  isAuthenticated: boolean
}

export default defineNuxtPlugin(async () => {
  const data = await useNuxtApp().$ofetch<AuthSession>('/api/auth-session')

  const store = useAuthSessionStore()
  store.user = data.user
  store.authorities = data.authorities
  store.isAuthenticated = data.isAuthenticated
})
