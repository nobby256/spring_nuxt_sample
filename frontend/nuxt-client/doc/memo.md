```
// `Options`の中から'value1'だけを抜き出し(`Pick`)、それを必須化(`Required`)する
type RequiredPart = Required<Pick<Options, 'value1'>>;
// これにより RequiredPart は `{ value1: string; }` となる

// `Options`の中から'value1'だけを取り除く(`Omit`)
type OptionalPart = Omit<Options, 'value1'>;
// これにより OptionalPart は `{ value2?: string; value3?: string; }` となる

// 2つのパーツを交差型(`&`)で合体させる
type Value1RequiredOptions = RequiredPart & OptionalPart;

// --- 使ってみる ---
const test1: Value1RequiredOptions = {
  value1: 'hello', // OK: 必須プロパティが存在する
};

const test2: Value1RequiredOptions = {
  value1: 'hello',
  value2: 'world', // OK: オプショナルプロパティはあっても良い
};

const test3: Value1RequiredOptions = {
  value2: 'world', // エラー！ プロパティ 'value1' は型にありません
};
```
```
/**
 * Tの中から、Kで指定されたキーのプロパティだけを必須(Required)にします。
 * @template T - 元の型
 * @template K - 必須にしたいプロパティのキー (Union型)
 */
type RequireFields<T, K extends keyof T> = Required<Pick<T, K>> & Omit<T, K>;
```
```
// RequireFieldsを使って、'value1'だけを必須にした型を生成
type MyOptions = RequireFields<Options, 'value1'>;

// 複数指定も可能！ 'value1'と'value2'を必須にする
type MyOptions2 = RequireFields<Options, 'value1' | 'value2'>;


// --- 使ってみる ---
const test4: MyOptions = { value1: "hello" }; // OK
const test5: MyOptions = { value2: "world" }; // エラー！ value1がない

const test6: MyOptions2 = { value1: "hello" }; // エラー！ value2がない
const test7: MyOptions2 = { value1: "hello", value2: "world" }; // OK
```