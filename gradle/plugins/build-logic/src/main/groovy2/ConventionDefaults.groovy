import org.gradle.api.Project
import org.gradle.api.GradleException
import org.gradle.api.resources.TextResource
import java.net.URL

class ConventionDefaults {
    
    static boolean getContinueOnError(Project project) {
        return project.hasProperty('continueOnError') ? 
               project.property('continueOnError').toBoolean() : false
    }
    
    /**
     * Checkstyle 設定ファイルを取得
     * 優先順位:
     * 1. プロジェクトルート/config/checkstyle/checkstyle.xml
     * 2. buildSrc/src/main/resources/checkstyle/checkstyle.xml
     */
    static File getCheckstyleConfigFile(Project project) {
        // 1. プロジェクトルートの config/checkstyle/checkstyle.xml
        def projectConfig = project.rootProject.file('config/checkstyle/checkstyle.xml')
        if (projectConfig.exists()) {
            return projectConfig
        }
        
        // 2. buildSrc 内のリソース
        def url = ConventionDefaults.class.getResource('/checkstyle/checkstyle.xml')
        if (url != null) {
            return project.resources.text.fromUri(url.toURI()).asFile()
        }
        
        throw new GradleException(
            "Checkstyle設定ファイルが見つかりません。\n" +
            "- ${projectConfig.absolutePath} または\n" +
            "- buildSrc/src/main/resources/checkstyle/checkstyle.xml\n" +
            "のいずれかに配置してください。"
        )
    }
    
    /**
     * SpotBugs 除外フィルタファイルを取得
     * 優先順位:
     * 1. プロジェクトルート/config/spotbugs/exclusion_filter.xml
     * 2. buildSrc/src/main/resources/spotbugs/exclusion_filter.xml
     */
    static File getSpotBugsExclusion(Project project) {
        // 1. プロジェクトルートの config/spotbugs/exclusion_filter.xml
        def projectConfig = project.rootProject.file('config/spotbugs/exclusion_filter.xml')
        if (projectConfig.exists()) {
            return projectConfig
        }
        
        // 2. buildSrc 内のリソース
        def url = ConventionDefaults.class.getResource('/spotbugs/exclusion_filter.xml')
        if (url != null) {
            return project.resources.text.fromUri(url.toURI()).asFile()
        }
        
        throw new GradleException(
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
    static String getNullAwayAnnotatedPackage(Project project) {
        // project.group が設定されていればそれを使う
        if (project.group) {
            return project.group.toString()
        }
        
        // 設定されていなければ、rootProject.group を使う
        if (project.rootProject.group) {
            return project.rootProject.group.toString()
        }
        
        // どちらもなければエラー
        throw new GradleException(
            "NullAway のパッケージを推測できません。\n" +
            "project.group または rootProject.group を設定するか、\n" +
            "nullaway { annotatedPackages.add('com.example') } を明示的に設定してください。"
        )
    }
}
