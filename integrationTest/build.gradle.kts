plugins {
    java
}

dependencies {
    compileOnly("org.jetbrains:annotations:21.0.1")
    compileOnly(project(":annotations"))
    annotationProcessor(project(":processor"))

    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("com.google.truth:truth:1.1.3") {
        exclude(group = "junit")
    }
}

tasks.test {
    useJUnitPlatform()
}
