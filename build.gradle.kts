import java.io.ByteArrayOutputStream

plugins {
    id("org.jetbrains.intellij.platform") version "2.2.0"
    id("groovy")
}

dependencies {
    sourceSets.named("test") {
        testImplementation("org.codehaus.groovy:groovy-all:2.4.14")
        testImplementation("org.opentest4j:opentest4j:1.3.0")
    }
    intellijPlatform {
        plugin("com.jetbrains.php:243.21565.193")
        plugin("com.jetbrains.twig:243.21565.202")
        bundledPlugin("org.jetbrains.plugins.yaml")
        bundledPlugin("com.intellij.css")
        bundledPlugin("JavaScript")
        create("IU","2024.3")
    }
}

buildscript {
    project.apply {
        from("$rootDir/config/extra-settings.gradle.kts")
    }
}

group = "com.oroplatform"
version = "1.1.1"

val pathToIde = project.extra["pathToIde"]

val javaVersionOutput = ByteArrayOutputStream()
exec {
    commandLine = listOf("java", "-version")
    standardOutput = javaVersionOutput
    errorOutput = javaVersionOutput
}

val javaVersionString = javaVersionOutput.toString().lines().first()
val javaVersion = Regex("""\d+""").find(javaVersionString)?.value?.toInt() ?: 11

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "idea-oroplatform-plugin"
    }
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

tasks {
    test {
        // OPP-80: The tests that target PHP code completion and JS completion
        // or those that use the information no longer present in classes.php in the newer version of the platform
        // are obsolete, as they assume that classes.php will store
        // data required for indexing, hence they should be removed from tests
        // Alek Mosingiewicz
        setExcludes(listOf(
                "*com/oroplatform/idea/oroplatform/intellij/codeAssist/javascript*",
                "*com/oroplatform/idea/oroplatform/intellij/codeAssist/yml/v1/AclPhpReferenceTest*",
        ))
    }
}
