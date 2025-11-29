import type { NitroFetchRequest, NitroFetchOptions } from 'nitropack'

export const apiFetch: ApiFetch = _apifetch

function _apifetch<
  T = unknown,
  R extends NitroFetchRequest = NitroFetchRequest,
  O extends NitroFetchOptions<R> = NitroFetchOptions<R>,
>(
  request: R,
  opts?: O,
): Promise<T> {
  return $apifetch(request, opts)
}
