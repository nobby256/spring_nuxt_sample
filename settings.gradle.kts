// =====================================================
// プラグインの共通設定を行います。
// =====================================================
pluginManagement {
    repositories {
        gradlePluginPortal()
    }
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
        id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
    }
    // 規約プラグインを登録します
    includeBuild("gradle/plugins/build-logic")
}

// =====================================================
// プラグインをロードします。
// =====================================================
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention")
}

// =====================================================
// プロジェクトの共通設定を行います。
// =====================================================
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

// =====================================================
// プロジェクトの名前を指定します
// =====================================================
rootProject.name = "demo"

// =====================================================
// サブプロジェクトを指定します
// =====================================================
include("backend:app")
project(":backend:app").name = "demo-app"
include("backend:lib")
project(":backend:lib").name = "demo-lib"
