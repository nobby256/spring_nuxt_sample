export type { ApiFetch } from '~/plugins/ApiFetchPlugin'

// NuxtAppを拡張して、グローバルな$apiFetchがこの正直な型を持つことを定義する
declare module '#app' {
  interface NuxtApp {
    $apiFetch: ApiFetch
  }
}
