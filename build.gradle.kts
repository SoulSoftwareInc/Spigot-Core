plugins {
    id("java")
    id("maven-publish")
    id("com.gradleup.shadow") version "8.3.5"
    `maven-publish`
}

group = "org.soulsoftware.spigot"
version = "1.0-SNAPSHOT"
description = "The core api for all SoulSoftware productions"


repositories {
    mavenCentral()
    mavenLocal()

    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://nexus.bencodez.com/repository/maven-public/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19.1-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.19.1-R0.1-SNAPSHOT")

    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.2.0-SNAPSHOT")

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")


    implementation("de.tr7zw:item-nbt-api:2.14.0")
    implementation("com.google.code.gson:gson:2.10")
    implementation("com.github.cryptomorin:XSeries:11.2.1")
    implementation("org.reflections:reflections:0.10.2")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    // Shade plugin configuration
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveClassifier.set("")

        relocate("de.tr7zw.changeme.nbtapi", "org.soulsoftware.spigot.Core.NBT")
        relocate("com.google.gson", "org.soulsoftware.spigot.Core.Gson")
        relocate("org.reflections", "org.soulsoftware.spigot.Core.Reflection")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "core"

            from(components["shadow"])
        }
    }

    repositories {
        mavenLocal()
        maven {
            name = "soulsoftwarePrivate"
            url = uri("https://maven.soulsoftware.dev/private")
            credentials {
                username = project.properties["soulsoftwareincPrivateUsername"] as String? ?: System.getenv("SOULSOFTWAREINC_PRIVATE_USERNAME")
                password = project.properties["soulsoftwareincPrivatePassword"] as String? ?: System.getenv("SOULSOFTWAREINC_PRIVATE_PASSWORD")
            }
            authentication {
                create("basic", BasicAuthentication::class.java)
            }
        }
    }
}