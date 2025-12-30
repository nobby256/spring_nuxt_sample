// =====================================================
// プロジェクト全体で共通するプラグインの設定を行います。
// =====================================================
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("org.jetbrains.kotlin.jvm") version "2.3.0"
    }
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
rootProject.name = "buildSrc"
