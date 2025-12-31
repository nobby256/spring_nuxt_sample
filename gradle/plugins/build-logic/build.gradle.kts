// =====================================================
// buildSrc を Gradle プラグインとしてビルドします
// =====================================================
plugins {
    id("groovy-gradle-plugin")
    `kotlin-dsl`
    kotlin("jvm")
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

    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.2.20")
}
