subprojects {
    repositories {
        mavenCentral()
    }

    plugins.withId("java") {
        configure<JavaPluginExtension> {
            toolchain.languageVersion.set(JavaLanguageVersion.of(16))
        }

        tasks.withType<JavaCompile>().configureEach {
            options.forkOptions.jvmArgs = listOf("--illegal-access=warn")
        }
    }
}
