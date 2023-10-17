plugins {
    id("org.jetbrains.intellij") version "1.14.2"
}


buildscript {
    project.apply {
        from("$rootDir/config/settings.gradle.kts")
    }
}

group = "com.oroplatform"
version = "2023.2"

val javaLanguageVersionSetting = project.extra["javaLanguageVersionSetting"].toString()

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaLanguageVersionSetting))
    }
}

intellij {
    pluginName.set("idea-oroplatform-plugin")
    type.set("IU")
    version.set("2023.2.3")
    plugins.set(listOf(
        "com.jetbrains.php:232.10072.27",
        "yaml",
        "java-i18n",
        "properties",
        "css-impl",
        "JavaScript",
        "com.jetbrains.twig:232.10072.32"
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
}
