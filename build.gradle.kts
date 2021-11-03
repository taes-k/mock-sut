plugins {
    java
    `maven-publish`
}

allprojects {
    repositories {
        mavenCentral()
        maven(url = "https://jitpack.io")
    }

    group = "io.github.taes-k"
    version = "0.0.2"
}

subprojects {
    apply {
        plugin("java")
        plugin("maven-publish")
    }

    java.sourceCompatibility = JavaVersion.VERSION_1_8

    sourceSets.main {
        java.srcDirs("src/main/java", "src/main/kotlin")
    }
}