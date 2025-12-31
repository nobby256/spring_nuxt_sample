// =====================================================
// Maven 公開設定（共通）
// =====================================================
// このプラグインは単体で使用するものではありません。
// 以下のプラグインから利用されることを想定しています。
//   - java-library-conventions
//   - spring-library-conventions
//   - platform-conventions
// =====================================================
plugins {
    maven-publish
}

// =====================================================
// 公開設定
// =====================================================
publishing {
    repositories {
        // ビルドした成果物は、maven repository の形式で以下のパスに配置される
        maven {
            name = "local-repo"
            url = uri(rootProject.layout.buildDirectory.dir("local-repo"))
        }
    }
}