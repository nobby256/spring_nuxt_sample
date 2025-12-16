/**
 * パラメータを文字列として取得します。
 * 例：
 * const id = getParamAsString(to.params.id, 'id')
 *
 * @param value
 * @param name
 * @param defaultValue
 * @returns
 */
export function getParamAsString(value: string | string[] | undefined, name = 'param', defaultValue?: string): string {
  if (value === undefined) {
    if (defaultValue !== undefined) return defaultValue
    throw createError({ statusCode: 400, statusMessage: `${name} is required` })
  }
  if (Array.isArray(value)) return value[0] ?? String(defaultValue ?? '')
  return value
}
