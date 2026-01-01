// =====================================================
// buildSrc を Gradle プラグインとしてビルドします
// =====================================================
plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
}

kotlin {
    jvmToolchain(21) // gradle 9.2.1時点ではgradli付属のkotlinはまだJVM25を未サポート
}

// =====================================================
// Convention Plugin で使用するプラグインを依存に追加します
// =====================================================
dependencies {
    implementation(libs.spring.boot.plugin)
    implementation(libs.spring.dependency.management.plugin)
    implementation(libs.spotbugs.plugin)
    implementation(libs.error.prone.plugin)
    implementation(libs.nullaway.plugin)

    implementation(libs.kotlin.stdlib)
}
