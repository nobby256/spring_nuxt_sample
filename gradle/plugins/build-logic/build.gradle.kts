// =====================================================
// buildSrc を Gradle プラグインとしてビルドします
// =====================================================
plugins {
    `kotlin-dsl`
    kotlin("jvm") version "2.2.20"
}

kotlin {
    jvmToolchain(21) // gradle 9.2.1時点ではgradli付属のkotlinはまだJVM25を未サポート
}

// =====================================================
// Convention Plugin で使用するプラグインを依存に追加します
// =====================================================
dependencies {
    // Kotlin DSL 用
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.20")

    // メインプロジェクトに提供するプラグイン
    implementation(libs.spring.boot.plugin)
    implementation(libs.spring.dependency.management.plugin)
    implementation(libs.spotbugs.plugin)
    implementation(libs.nullability.plugin)
    implementation(libs.spotless.plugin)

    implementation(libs.error.prone.plugin)
    implementation(libs.nullaway.plugin)
}
