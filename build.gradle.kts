import java.io.ByteArrayOutputStream

plugins {
    id("org.jetbrains.intellij") version "1.17.4"
    id("groovy")
}


dependencies {
    sourceSets.named("test") {
        testImplementation("org.codehaus.groovy:groovy-all:2.4.14")
        testImplementation("org.opentest4j:opentest4j:1.3.0")
    }
}

buildscript {
    project.apply {
        from("$rootDir/config/extra-settings.gradle.kts")
    }
}

group = "com.oroplatform"
version = "1.1.1"



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

intellij {
    pluginName.set("idea-oroplatform-plugin")
    type.set("IU")
    version.set("2024.3")
    plugins.set(listOf(
        "com.jetbrains.php:243.21565.193",
        "yaml",
        "java-i18n",
        "properties",
        "css-impl",
        "JavaScript",
        "com.jetbrains.twig:243.21565.202"
    ))
    sandboxDir.set("${project.rootDir}/.idea-sandbox")
}

repositories {
    mavenCentral()
}

tasks {
    runIde {
        val pathToIde = project.extra["pathToIde"]
        ideDir.set(file("$pathToIde"))
    }
    buildSearchableOptions {
        enabled = true
    }
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
