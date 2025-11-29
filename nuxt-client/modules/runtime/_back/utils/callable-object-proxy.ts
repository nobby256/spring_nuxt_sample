/**
 * 差し替え可能な実体への参照を、安全にプロキシするオブジェクトを生成する。
 *
 * @param getInstance 現在の実体を返すゲッター関数
 * @returns あらゆるアクセスを`getInstance()`が返す最新の実体に転送するProxy
 */
export function createCallableObjectProxy<T extends object>(getInstance: () => T): T {
  const isCallable = typeof getInstance() === 'function'
  const target = (isCallable ? function () {} : {}) as T

  const proxyHandler: ProxyHandler<T> = {
    apply: isCallable
      ? (target, thisArg, argumentsList) => {
          // このジェネリックな文脈では`as Function`のキャストが不可欠なため、Lintルールを意図的に無効化する
          // eslint-disable-next-line @typescript-eslint/no-unsafe-function-type
          return Reflect.apply(getInstance() as Function, thisArg, argumentsList)
        }
      : undefined,

    get(target, prop, receiver) {
      return Reflect.get(getInstance(), prop, receiver)
    },
  }

  return new Proxy(target, proxyHandler)
}
