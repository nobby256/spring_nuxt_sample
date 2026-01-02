import io.spring.gradle.nullability.NullabilityOptions;

// =====================================================
// Spring Boot プロジェクトの基本設定
// =====================================================
plugins {
    id("java-conventions")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("io.spring.nullability")
}

// =====================================================
// Spring Boot 設定
// =====================================================
// 実行可能 JAR（bootJar）を生成する
tasks.bootJar {
    enabled = true
}
// 通常の JAR は生成しない
tasks.jar {
    enabled = false
}

// =====================================================
// Configuration 設定
// =====================================================
configurations {
    // アノテーションプロセッサをコンパイル時のクラスパスに含める
    // この指定があるとcompileOnlyの依存関係を省略できる（記述されていても良い）
    // compileOnly("org.projectlombok:lombok") ← 省略しても良くなる。
    // annotationProcessor("org.projectlombok:lombok")
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
    testCompileOnly {
        extendsFrom(configurations.testAnnotationProcessor.get())
    }
}

// =====================================================
// 依存関係
// =====================================================
dependencies {
    // Spring Boot の @ConfigurationProperties のメタデータ生成
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    
    // 開発時の自動リロード（本番では自動的に無効化される）
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

// =====================================================
// Nullability 設定
// =====================================================
tasks.withType<JavaCompile>().configureEach {
    if (project.findProperty("nullability.enabled")?.toString()?.toBoolean() == true) {
        if (name != "compileJava") {
            // mainソースセット以外は@NullMarkedを強要しない
            val nullabilityOptions = (options as ExtensionAware).extensions.getByName("nullability") as NullabilityOptions
            nullabilityOptions.requireExplicitNullMarking.set(false)
            // kotlinで下記の実装が出来るようにプラグインのバージョンアップ待ち
            //options.nullability.requireExplicitNullMarking.set(false)
        }
    } else {
        val nullabilityOptions = (options as ExtensionAware).extensions.getByName("nullability") as NullabilityOptions
        nullabilityOptions.checking.set("disabled")
    }
}
