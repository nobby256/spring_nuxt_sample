// =====================================================
// プロジェクト全体で共通するプラグインの設定を行います。
// =====================================================
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

// =====================================================
// プラグインをロードします。
// =====================================================
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

// =====================================================
// プロジェクト全体で共通する設定を行います。
// =====================================================
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../../libs.versions.toml"))
        }
    }
}

// =====================================================
// プロジェクトの名前を指定します
// =====================================================
rootProject.name = "build-logic"
