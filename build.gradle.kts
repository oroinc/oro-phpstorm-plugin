import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.intellij.platform") version "2.7.2"
    id("groovy")
}

group = "com.oroplatform"
version = "2025.2.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

java { toolchain { languageVersion.set(JavaLanguageVersion.of(21)) } }

intellijPlatform {
    pluginConfiguration {
        name = "OroCommerce / OroPlatform"
    }
}

dependencies {
    intellijPlatform {
        phpstorm("2025.2")
        bundledPlugin("com.jetbrains.php")
        bundledPlugin("com.jetbrains.twig")
        bundledPlugin("org.jetbrains.plugins.yaml")
        bundledPlugin("com.intellij.css")
        bundledPlugin("JavaScript")
        testFramework(TestFrameworkType.Platform)
    }
    testImplementation("org.codehaus.groovy:groovy-all:3.0.19")
}

tasks {
    test {
        // TODO Refactor/remove obsolete tests (OPP-80 ?) - verify the following and current state of tests:
        // TODO Tests that target PHP and Javascript code completion or rely on the data required for indexing
        // TODO that is no longer present in classes.php in current Oro versions are obsolete.
        setExcludes(listOf(
            "**/com/oroplatform/idea/oroplatform/intellij/codeAssist/javascript/**",
            "**/com/oroplatform/idea/oroplatform/intellij/codeAssist/yml/v1/AclPhpReferenceTest*",
        ))
    }
}

val extras = rootProject.file("config/extra-settings.gradle.kts")
if (extras.isFile) {
    apply(from = extras)
}
