// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
  val kotlin_version by extra("1.5.10")
  repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://plugins.gradle.org/m2/")
  }
  dependencies {
    classpath("com.android.tools.build:gradle:4.0.1")
    classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
    classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
    classpath("com.github.dcendents:android-maven-plugin:1.2")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts files
    }
}

subprojects {
    repositories {
        google()
        mavenCentral()
        maven ("https://jitpack.io")
    }
}


tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
