plugins {
    id("org.jetbrains.intellij") version "1.14.2"
    id("groovy")
}


dependencies {
    sourceSets.named("test") {
        implementation("junit:junit:4.11")
        testImplementation("org.junit.platform:junit-platform-launcher")
        testRuntimeOnly("org.junit.vintage:junit-vintage-engine") {
            because("allows JUnit 3 and JUnit 4 tests to run")
        }
        testImplementation("org.spockframework:spock-core:2.4-M1-groovy-4.0") {
            because("allows Spock specifications to run")
        }
        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testImplementation("org.junit.jupiter:junit-jupiter")
    }
}

java.sourceSets["test"].java {
    srcDir("src/test/groovy")
}

buildscript {
    project.apply {
        from("$rootDir/config/extra-settings.gradle.kts")
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
    test {
        useJUnitPlatform() {
            includeEngines("junit-vintage", "junit-jupiter")
        }
    }
}
