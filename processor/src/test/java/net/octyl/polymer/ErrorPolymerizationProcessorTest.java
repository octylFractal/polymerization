package net.octyl.polymer;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;


import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

public class ErrorPolymerizationProcessorTest {

    @Test
    void missingSetter() {
        var source = JavaFileObjects.forSourceString(
            "Foo",
            // language=java
            """
            import net.octyl.polymer.PolymerizeApi;
            record Foo(String bar) {
                @PolymerizeApi
                interface Builder {
                    Foo build();
                }
            }
            """
        );
        var compilation = javac()
            .withProcessors(new PolymerizationProcessor())
            .compile(source);

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorCount(1);
        assertThat(compilation).hadErrorContaining("Builder is missing a setter for bar")
            .inFile(source)
            .onLine(4);
    }

    @Test
    void missingBuild() {
        var source = JavaFileObjects.forSourceString(
            "Foo",
            // language=java
            """
            import net.octyl.polymer.PolymerizeApi;
            record Foo(String bar) {
                @PolymerizeApi
                interface Builder {
                    Builder bar(String value);
                }
            }
            """
        );
        var compilation = javac()
            .withProcessors(new PolymerizationProcessor())
            .compile(source);

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorCount(1);
        assertThat(compilation).hadErrorContaining("Builder is missing a build-like method")
            .inFile(source)
            .onLine(4);
    }

    @Test
    void separateBuilderFileNoResult() {
        var source = JavaFileObjects.forSourceString(
            "Foo",
            // language=java
            """
            record Foo(String bar) {
            }
            """
        );
        var sourceBuilder = JavaFileObjects.forSourceString(
            "FooBuilder",
            // language=java
            """
            import net.octyl.polymer.PolymerizeApi;
            @PolymerizeApi
            interface FooBuilder {
                FooBuilder bar(String value);

                Foo build();
            }
            """
        );
        var compilation = javac()
            .withProcessors(new PolymerizationProcessor())
            .compile(source, sourceBuilder);

        assertThat(compilation).failed();
        assertThat(compilation).hadErrorCount(1);
        assertThat(compilation).hadErrorContaining("Builder is not enclosed by a record")
            .inFile(sourceBuilder)
            .onLine(3);
    }
}
