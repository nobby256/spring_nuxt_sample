// =====================================================
// Spring Boot ライブラリプロジェクトの設定
// =====================================================
plugins {
    java-library
    id("publish-conventions")
    id("springboot-conventions")
    id("io.spring.dependency-management")
}

// =====================================================
// Spring Boot 設定
// =====================================================
// 実行可能 JAR（bootJar）を生成しない
tasks.bootJar {
    enabled = false
}
// 通常の JAR は生成する
tasks.jar {
    enabled = true
    // archiveClassifier を空にして、-plain を消す
    archiveClassifier.set("")
}

// =====================================================
// Maven 公開設定
// =====================================================
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            // Spring Boot の BOM で管理されている依存関係のバージョンを POM に記録する
            // 例: implementation("org.springframework:spring-context") とバージョン指定なしで宣言しても
            //     POM には <version>6.2.1</version> のように解決されたバージョンが記載される
            versionMapping {
                // api(...) として公開する依存関係のバージョンを解決
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                // implementation(...) として公開するの依存関係のバージョンを解決
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
}