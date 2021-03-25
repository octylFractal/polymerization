plugins {
    java
}

dependencies {
    compileOnly("org.jetbrains:annotations:20.1.0")
    compileOnly(project(":annotations"))
    annotationProcessor(project(":processor"))

    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("com.google.truth:truth:1.1.2") {
        exclude(group = "junit")
    }
}

tasks.test {
    useJUnitPlatform()
}
