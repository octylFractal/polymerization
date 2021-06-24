plugins {
    java
}

java {
    withSourcesJar()
    // no javadoc jar, the API isn't public
}

tasks.jar {
    // TODO move to a real module once auto-common does
    manifest.attributes("Automatic-Module-Name" to "net.octyl.polymer.processor")
}

configureMavenPublication()

dependencies {
    compileOnly("org.jetbrains:annotations:21.0.1")

    val autoServiceVersion = "1.0"
    compileOnly("com.google.auto.service:auto-service-annotations:$autoServiceVersion")
    annotationProcessor("com.google.auto.service:auto-service:$autoServiceVersion")

    implementation("com.google.auto:auto-common:1.0.1")

    implementation("com.google.guava:guava:30.1.1-jre")

    implementation("com.squareup:javapoet:1.13.0")

    implementation(project(":annotations"))

    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("com.google.testing.compile:compile-testing:0.19")
    testImplementation("com.google.truth:truth:1.1.3") {
        exclude(group = "junit")
    }
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("--add-opens=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED")
}
