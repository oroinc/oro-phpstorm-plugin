plugins {
    id("org.jetbrains.intellij") version "1.11.0"
}

group = "com.oroplatform"
version = "2022.3"

intellij {
    pluginName.set("idea-oroplatform-plugin")
    type.set("IU")
    version.set("2022.3")
    plugins.set(listOf(
        "com.jetbrains.php:223.7571.212",
        "yaml",
        "java-i18n",
        "properties",
        "css-impl",
        "JavaScript",
        "com.jetbrains.twig:223.7571.212"
    ))
    sandboxDir.set("${project.rootDir}/.idea-sandbox")
}

repositories {
    mavenCentral()
}

tasks {
    runIde {
        ideDir.set(file("/home/michael/Programs/PhpStorm-223.7571.212"))
    }
}
