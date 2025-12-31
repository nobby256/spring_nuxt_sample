// =====================================================
// NullAway による Null 安全性チェック設定
// =====================================================
// デフォルトでは project.group をチェック対象パッケージとして使用します。
// カスタマイズしたい場合は、各プロジェクトで上書きできます:
//   nullaway {
//       annotatedPackages.clear()
//       annotatedPackages.add("com.example.custom")
//   }
// =====================================================
plugins {
    id("net.ltgt.errorprone")
    id("net.ltgt.nullaway")
}

// =====================================================
// 依存関係
// =====================================================
dependencies {
    errorprone("com.google.errorprone:error_prone_core:2.45.0")
    errorprone("com.uber.nullaway:nullaway:0.12.15")
}

// =====================================================
// コンパイル設定
// =====================================================
tasks.withType<JavaCompile>().configureEach {
    if (name == "compileTestJava") {
        options.errorprone.enabled = false
    } else {
        options.errorprone.nullaway {
            warn()
            jspecifyMode.set(true)
        }
    }
}

// =====================================================
// NullAway 設定
// =====================================================
nullaway {
    // プロジェクトの group から自動的にパッケージを設定
    annotatedPackages.add(ConventionDefaults.getNullAwayAnnotatedPackage(project))
}