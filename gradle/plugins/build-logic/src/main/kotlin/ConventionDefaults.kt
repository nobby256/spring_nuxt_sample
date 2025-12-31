import org.gradle.api.Project
import org.gradle.api.GradleException
import org.gradle.api.resources.TextResource
import java.io.File

object ConventionDefaults {
    @JvmStatic
    fun getContinueOnError(project: Project): Boolean {
        return project.findProperty("continueOnError")?.toString()?.toBoolean() ?: false
    }

    /**
     * Checkstyle 設定ファイルを取得
     * 優先順位:
     * 1. プロジェクトルート/config/checkstyle/checkstyle.xml
     * 2. buildSrc/src/main/resources/checkstyle/checkstyle.xml
     */
    @JvmStatic
    fun getCheckstyleConfig(project: Project): TextResource {
        val projectConfig = project.rootProject.file("config/checkstyle/checkstyle.xml")
        if (projectConfig.exists()) {
            // FileをTextResourceに変換
            return project.resources.text.fromFile(projectConfig)
        }

        // リソースからTextResourceを取得
        val resourceUrl = ConventionDefaults::class.java.getResource("/checkstyle/checkstyle.xml")
        if (resourceUrl != null) {
            return project.resources.text.fromUri(resourceUrl)
        }

        throw GradleException(
            "Checkstyle設定ファイルが見つかりません。\n" +
                "- ${projectConfig.absolutePath} または\n" +
                "- build-logic/src/main/resources/checkstyle/checkstyle.xml\n" +
                "のいずれかに配置してください。"
        )
    }

    /**
     * SpotBugs 除外フィルタファイルを取得
     * 優先順位:
     * 1. プロジェクトルート/config/spotbugs/exclusion_filter.xml
     * 2. buildSrc/src/main/resources/spotbugs/exclusion_filter.xml
     */
    @JvmStatic
    fun getSpotBugsExclusion(project: Project): File {
        val projectConfig = project.rootProject.file("config/spotbugs/exclusion_filter.xml")
        if (projectConfig.exists()) {
            return projectConfig
        }

        val url = ConventionDefaults::class.java.getResource("/spotbugs/exclusion_filter.xml")
        if (url != null) {
            return File(url.toURI())
        }

        throw GradleException(
            "SpotBugs除外フィルタファイルが見つかりません。\n" +
                "- ${projectConfig.absolutePath} または\n" +
                "- buildSrc/src/main/resources/spotbugs/exclusion_filter.xml\n" +
                "のいずれかに配置してください。"
        )
    }

    /**
     * NullAway のチェック対象パッケージを取得
     * プロジェクトの group からパッケージ名を推測する
     */
    @JvmStatic
    fun getNullAwayAnnotatedPackage(project: Project): String {
        project.group?.let { return it.toString() }
        project.rootProject.group?.let { return it.toString() }

        throw GradleException(
            "NullAway のパッケージを推測できません。\n" +
                "project.group または rootProject.group を設定するか、\n" +
                "nullaway { annotatedPackages.add('com.example') } を明示的に設定してください。"
        )
    }
}
