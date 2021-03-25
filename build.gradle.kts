import org.cadixdev.gradle.licenser.LicenseExtension

plugins {
    id("io.github.gradle-nexus.publish-plugin") version "1.0.0"
    id("org.cadixdev.licenser") version "0.5.1" apply false
}

subprojects {
    apply(plugin = "org.cadixdev.licenser")

    repositories {
        mavenCentral()
    }

    configure<LicenseExtension> {
        header = rootProject.file("HEADER.txt")
        (this as ExtensionAware).extra.apply {
            for (key in listOf("organization", "url")) {
                set(key, rootProject.property(key))
            }
        }
    }

    plugins.withId("java") {
        configure<JavaPluginExtension> {
            toolchain.languageVersion.set(JavaLanguageVersion.of(16))
        }

        tasks.withType<JavaCompile>().configureEach {
            options.forkOptions.jvmArgs = listOf("--illegal-access=warn")
        }

        tasks.withType<Test>().configureEach {
            jvmArgs("--illegal-access=warn")
        }
    }
}

nexusPublishing {
    repositories {
        sonatype()
    }
}
