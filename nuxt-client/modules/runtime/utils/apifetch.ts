import type { NitroFetchRequest, NitroFetchOptions } from 'nitropack'

export function apifetch<
  T = unknown,
  R extends NitroFetchRequest = NitroFetchRequest,
  O extends NitroFetchOptions<R> = NitroFetchOptions<R>,
>(
  request: R,
  opts?: O,
): Promise<T> {
  return useNuxtApp().$apifetch(request, opts) as Promise<T>
}
