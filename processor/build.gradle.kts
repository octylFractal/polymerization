plugins {
    java
}

java {
    withSourcesJar()
    // no javadoc jar, the API isn't public
}

configureMavenPublication()

dependencies {
    compileOnly("org.jetbrains:annotations:20.1.0")

    val autoServiceVersion = "1.0-rc7"
    compileOnly("com.google.auto.service:auto-service-annotations:$autoServiceVersion")
    annotationProcessor("com.google.auto.service:auto-service:$autoServiceVersion")

    implementation("com.google.auto:auto-common:0.11")

    implementation("com.google.guava:guava:30.1.1-jre")

    implementation("com.squareup:javapoet:1.13.0")

    implementation(project(":annotations"))

    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("com.google.testing.compile:compile-testing:0.19")
    testImplementation("com.google.truth:truth:1.1.2") {
        exclude(group = "junit")
    }
}

tasks.test {
    useJUnitPlatform()
}
