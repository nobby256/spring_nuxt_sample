// =====================================================
// Java ライブラリプロジェクトの設定
// =====================================================
plugins {
    id("java-conventions")
    java-library
    id("publish-conventions")
}


// =====================================================
// Maven 公開設定
// =====================================================
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }
}
