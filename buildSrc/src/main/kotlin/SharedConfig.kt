import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.the
import org.gradle.plugins.signing.SigningExtension
import java.util.concurrent.Callable

fun Project.configureMavenPublication() {
    apply {
        plugin("maven-publish")
        plugin("signing")
    }

    the<PublishingExtension>().publications {
        register<MavenPublication>("default") {
            from(components["java"])
            artifactId = "${rootProject.name}-${project.name}"
        }
    }

    configure<SigningExtension> {
        setRequired(Callable {
            System.getenv("SIGNING_REQUIRED")?.toBoolean() == true
        })
        useInMemoryPgpKeys(
            System.getenv("POLYMERIZATION_SIGNING_KEY"),
            System.getenv("POLYMERIZATION_SIGNING_PASSWORD")
        )
        sign(the<PublishingExtension>().publications)
    }
}
