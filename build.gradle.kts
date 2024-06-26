plugins {
    id("org.jetbrains.intellij") version "1.14.2"
    id("groovy")
}


dependencies {
    sourceSets.named("test") {
        testImplementation("org.codehaus.groovy:groovy-all:2.4.14")
    }
}

buildscript {
    project.apply {
        from("$rootDir/config/extra-settings.gradle.kts")
    }
}

group = "com.oroplatform"
version = "1.1.0"

val javaLanguageVersionSetting = project.extra["javaLanguageVersionSetting"].toString()

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaLanguageVersionSetting))
    }
}

intellij {
    pluginName.set("idea-oroplatform-plugin")
    type.set("IU")
    version.set("2024.1")
    plugins.set(listOf(
        "com.jetbrains.php:241.14494.237",
        "yaml",
        "java-i18n",
        "properties",
        "css-impl",
        "JavaScript",
        "com.jetbrains.twig:241.14494.237"
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
