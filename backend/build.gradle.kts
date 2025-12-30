import org.gradle.api.plugins.quality.Checkstyle
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    id("nullaway-conventions") apply false
}

subprojects {
    apply(plugin = "io.spring.dependency-management")
    configure<DependencyManagementExtension> {
        imports {
            mavenBom(SpringBootPlugin.BOM_COORDINATES)
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.0.0")
            mavenBom("org.springdoc:springdoc-openapi-bom:3.0.0")
        }
        dependencies {
            dependency("org.apache.commons:commons-lang3:3.20.0")
        }
    }

    apply(plugin = "checkstyle")
    tasks.withType<Checkstyle> {
        isEnabled = false
    }

    apply(plugin = "nullaway-conventions")
}
