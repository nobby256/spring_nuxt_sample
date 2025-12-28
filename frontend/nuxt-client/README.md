# クライアント側のサンプルアプリについて

本プロジェクトは、S-Cred⁺ フレームワークを利用した SPA のクライアント側のサンプルアプリプロジェクトである。

## 1：概要

本アプリは以下の機能のサンプルコードを準備している。  
各機能の詳細についてはソースコード内のコメントやページ内の記述を参照すること。  
また、S-Cred⁺ フレームワークのドキュメントについても、合わせて参照すること。

- CRUD 形式の画面パターンサンプル
- ウィザード形式の画面パターンサンプル
- エラーハンドリングのサンプル

## プロジェクトのディレクトリ構成

本プロジェクトのディレクトリ構成は下記の通り。
（Nuxt4の標準ディレクトリ構成準拠）

```
+ client-nuxt (プロジェクトのルート)
  + .vscode (VSCodeの設定ファイルを配置)
  + app
    + components (コンポーネントを配置)
    + composables (コンポーザブルを配置)
    + layouts (レイアウトファイルを配置)
    + middleware (ミドルウェアを配置)
    + pages (ページごとのファイルを配置)
      + crud (CRUD画面)
        + index
      + errorhandling (エラーハンドリング画面)
        + index
      + wizard (ウィザード画面)
        + index
    + plugins (プラグインを配置)
    + utils (ユーティリティを配置)
    + store (アプリケーション全体で共有するPiniaストア)
  + public (ビルドされないhtmlファイルや画像等を配置)
```

## 2：プロジェクトのオープン

下記は本アプリ全体のディレクトリ構成だが、VSCodeでオープンする為には`client-nuxt`ディレクトリを直接オープンする事。

```
+ scfw-sample-spa-project
  + bff-springboot
  + client-nuxt ⇐ このディレクトリを直接オープン
```

## 3：開発手順

本プロジェクトのルートディレクトリのREADME.mdで説明した通り、本プロジェクトは下記の実行形態を提供している。

1. 運用形態

   SpringBoot実行可能JARの中にSPAクライアントを含めた本来の実行形態。  
   運用時および結合テスト以降の工程で利用する。

2. 開発形態

   SpringBoot（サーバ）とNuxt開発モード（クライアント）を連携させる実行形態。  
   SPAクライアントの生産性を上げる為、SPAクライアントをJARの中に含めずに実行出来る。

以降ではそれぞれの実行形態のビルド手順を説明する。

## 4：運用形態のビルド方法

運用形態の場合、本手順のゴールはビルド成果物をJavaプロジェクトの`/resources/public`にコピーする事となる。

```
npm install ⇐ 初回１回だけで良い
npm run generate
```

上記のコマンドはSPAクライアントのビルドとビルド成果物をJavaプロジェクトにコピーすることを行う。  
アプリケーションの実行はJavaプロジェクト側でIDEによる実行、もしくはJavaプロジェクトをビルドすることで出来る実行可能JARの起動によって行う。

## 5：開発形態のビルド方法

この形態ではSpringBoot（サーバー）とNuxt開発モード（クライアント）を同時に起動することで、SPAクライアントをJARの中に含めずに開発することができる。  
この形態の目的はビルド成果物の作成ではなく、SPAクライアントをTypeScriptとしてでデバッグできるNuxt開発サーバーの起動となる。

```
npm install ⇐ 初回１回だけで良い
npm run dev
```

ターミナルで下記の出力が確認出来たらNuxtの起動は完了。  

```
$ npm run dev

> dev
> nuxt dev

Nuxt 4.2.1 (with Nitro 2.12.9, Vite 7.2.2 and Vue 3.5.24)

  ➜ Local:    http://localhost:3000/
  ➜ Network:  use --host to expose

ℹ Using yup with vee-validate
  ➜ DevTools: press Shift + Alt + D in the browser (v3.1.0)


 WARN  Slow module @nuxt/devtools took 7222.98ms to setup.

✔ Vite client built in 437ms
✔ Vite server built in 1094ms
✔ Nuxt Nitro server built in 13192ms
ℹ Vite client warmed up in 2ms

[vue-tsc] Found 0 errors. Watching for file changes.
```

SpringBoot側も同時に起動している状態で、ブラウザに`http://localhost:8080`を入力することで実行出来る。  
ID:  user  
PWD: password

注意点
- Nuxt単体ではシステムとして機能しない（サーバーが起動していないとエラーになる）
- 直接ブラウザから`http://localhost:3000/`を入力する事は無い
