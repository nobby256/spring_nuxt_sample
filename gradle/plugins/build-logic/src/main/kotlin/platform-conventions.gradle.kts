// =====================================================
// BOM（Bill of Materials）プロジェクトの設定
// =====================================================
plugins {
    `java-platform`
    id("publish-conventions")
}

// =====================================================
// Maven 公開設定
// =====================================================
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["javaPlatform"])
        }
    }
}