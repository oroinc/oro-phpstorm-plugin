# Prerequirements

[Gradle][1] is used as build tool for this project, so you have to install [it][1]. Of course jdk is also required.

## How to build plugin?

* Run `./gradlew buildPlugin` - first run will take a long time - gradle at the beginning will be downloading whole IntelliJ SDK
* Plugin zip should be available in `build/distributions` directory

## How to run tests

Tests can be run using `./gradlew test`.

## How to run testing intellij instance with the plugin installed?

Just run `./gradlew runIdea`. First time you will get error, something about "PHP plugin is required" etc. So you have to
install "PHP plugin" manually and restart testing IntelliJ instance.

[1]: http://gradle.org/gradle-download/