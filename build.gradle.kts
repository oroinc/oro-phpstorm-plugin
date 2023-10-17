plugins {
    id("org.jetbrains.intellij") version "1.14.2"
}

group = "com.oroplatform"
version = "2023.1"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

intellij {
    pluginName.set("idea-oroplatform-plugin")
    type.set("IU")
    version.set("2023.1")
    plugins.set(listOf(
        "com.jetbrains.php:231.8109.51",
        "yaml",
        "java-i18n",
        "properties",
        "css-impl",
        "JavaScript",
        "com.jetbrains.twig:231.8109.78"
    ))
    sandboxDir.set("${project.rootDir}/.idea-sandbox")
}

repositories {
    mavenCentral()
}

tasks {
    runIde {
        ideDir.set(file("/home/aleksander/programs/PhpStorm-231.8109.199"))
    }
    buildSearchableOptions {
        enabled = true
    }
}
