// 1. vue-routerモジュールを拡張することをTypeScriptに伝える
import 'vue-router';
import type { RouteLocationNormalized } from 'vue-router';

// 2. モジュール宣言でvue-routerを拡張する
declare module 'vue-router' {
  // 3. RouteMetaインターフェースに新しいプロパティを追加する
  interface RouteMeta {
    /**
     * ページ描画前に実行されるデータ取得関数。
     * この関数が定義されているルートでのみ、グローバルミドルウェアが処理を実行する。
     */
    fetchData?: (to: RouteLocationNormalized) => Promise<void>;
    // 他にも、例えば権限情報などを追加できる
    // requiredAuth?: boolean;
  }
}