// =====================================================
// Java プロジェクトの基本設定
// =====================================================
plugins {
    java
    jacoco
    checkstyle
    id("com.github.spotbugs")
}

// =====================================================
// 共通設定から取得
// =====================================================
val continueOnError = ConventionDefaults.getContinueOnError(project)

// =====================================================
// Java ツールチェーン設定
// =====================================================
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    withSourcesJar()
    withJavadocJar()
}

// =====================================================
// テスト設定
// =====================================================
tasks.test {
    useJUnitPlatform()

    // テストエラーをビルドエラーとしない
    ignoreFailures = continueOnError
    finalizedBy(tasks.jacocoTestReport)

    reports {
        // ローカルはeclipse or html、CIはcodebuild/jenkinsで確認
        html.required.set(true)
        junitXml.required.set(true)
        junitXml.isOutputPerTestCase = true
        junitXml.mergeReruns = true
    }
}

// =====================================================
// ソースセット設定
// =====================================================
sourceSets {
    main {
        java {
            exclude("**/.gitkeep")
        }
        resources {
            exclude("**/.gitkeep")
        }
    }
    test {
        java {
            exclude("**/.gitkeep")
        }
        resources {
            exclude("**/.gitkeep")
        }
    }
}

// =====================================================
// コンパイル設定
// =====================================================
tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-Xlint:deprecation,unchecked"))
}

// =====================================================
// Javadoc 設定
// =====================================================
tasks.withType<Javadoc>().configureEach {
    isFailOnError = !continueOnError
    
    (options as StandardJavadocDocletOptions).apply {
        locale = "ja_JP"
        encoding = "UTF-8"

        // javadoc側ではコメントの存在チェックを行わないようにする。
        // （存在した場合の表記チェックはそのまま残す）
        // 切っ掛けはJDK21のjavadocでデフォルトコンストラクタの存在が強要されるようになった事。
        // このチェックはデフォルトコンストラクタが定義されているがコメントは無いという状態を警告するのではなく、
        // デフォルトコンストラクタの定義が無くても警告が発せられる。
        // つまり、この警告を抑えるにはデフォルトコンストラクタを作成し、コメントも記述しなければならない。
        // しかしそれではlombokを積極的に使っているアプリ側は非常に困る。
        // そこで、javadocではコメントの存在チェックを行わず、checkstyle側でコメントの存在チェックを行うことにした。
        // checkstyleはソースコードとして定義されているものにしかチェックを行わない為、lombokが生成するコードはチェック対象外となる。
        addBooleanOption("Xdoclint:all,-missing", true)
    }
}

// =====================================================
// Jacoco 設定
// =====================================================
jacoco {
    toolVersion = "0.8.13"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        // ローカルはeclipse or html、CIはcodebuild/jenkinsで確認
        html.required.set(true)  // .set() を使用
        xml.required.set(true)
    }
}

// =====================================================
// Checkstyle 設定
// =====================================================
checkstyle {
    toolVersion = "10.26.1"
    isIgnoreFailures = continueOnError
    config = ConventionDefaults.getCheckstyleConfig(project)
}

// テストに対しては実行しない
tasks.checkstyleTest {
    enabled = false
}

// =====================================================
// SpotBugs 設定
// =====================================================
spotbugs {  // 型安全な設定
    ignoreFailures.set(continueOnError)
    excludeFilter.set(ConventionDefaults.getSpotBugsExclusion(project))
    showProgress.set(false)
}

// テストに対しては実行しない
tasks.spotbugsTest {
    enabled = false
}

tasks.spotbugsMain {
    // ローカルはeclipse or html、CIはcodebuild/jenkinsで確認
    reports {
        create("xml") {
            required.set(true)  // .set() を使用
        }
        create("html") {
            required.set(true)
            setStylesheet("fancy-hist.xsl")
        }
    }
}

// =====================================================
// 依存関係
// =====================================================
dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1") // ← org.springframework.boot:spring-boot-starter-testでも追加してくれる
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.1")
}
