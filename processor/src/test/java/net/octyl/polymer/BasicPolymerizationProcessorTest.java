package net.octyl.polymer;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;


import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

public class BasicPolymerizationProcessorTest {
    private static final ZonedDateTime FORCED_DATE =
        ZonedDateTime.of(2021, 3, 24, 0, 0, 0, 0, ZoneOffset.UTC);

    private static final JavaFileObject TEST_NULLABLE = new SourceFile(
        "test.Nullable",
        // language=java
        """
        package test;
        public @interface Nullable {}
        """
    ).toFileObject();

    @Test
    void testBasicRecord() throws IOException {
        var source = new SourceFile(
            "Foo",
            // language=java
            """
            import net.octyl.polymer.PolymerizeApi;
            import test.Nullable;
            record Foo(String bar, Object bag, @Nullable String nullString, int bazzes) {
                @PolymerizeApi
                interface Builder {
                    Builder bar(String value);

                    Builder bag(Object value);

                    Builder nullString(@Nullable String value);

                    Builder bazzes(int value);

                    Foo build();
                }
            }
            """
        );
        var compilation = javac()
            .withProcessors(new PolymerizationProcessor(FORCED_DATE))
            .compile(source.toFileObject(), TEST_NULLABLE);
        assertThat(compilation).succeededWithoutWarnings();

        JavaFileObject builderClass = compilation.generatedFile(
            StandardLocation.CLASS_OUTPUT, "", "PolymerizeImplFoo_Builder.class"
        ).orElseThrow();
        try (var inputStream = builderClass.openInputStream();
             var outputStream = Files.newOutputStream(Path.of("./build/PolymerizeImplFoo_Builder.class"))) {
            inputStream.transferTo(outputStream);
        }

        assertThat(compilation).generatedSourceFile("PolymerizeImplFoo_Builder")
            .hasSourceEquivalentTo(new SourceFile(
                "PolymerizeImplFoo_Builder",
                // language=java
                """
                import java.lang.IllegalArgumentException;
                import java.lang.IllegalStateException;
                import java.lang.Object;
                import java.lang.Override;
                import java.lang.String;
                import javax.annotation.processing.Generated;
                import test.Nullable;

                @Generated(
                    value = "net.octyl.polymer.PolymerizationProcessor",
                    date = "%s"
                )
                final class PolymerizeImplFoo_Builder implements Foo.Builder {
                  private String bar;
                  private Object bag;
                  private @Nullable String nullString;
                  private int bazzes;

                  @Override
                  public Foo.Builder bar(String value) {
                    if (value == null) {
                      throw new IllegalArgumentException("'".concat("bar").concat("'").concat(" cannot be null"));
                    }
                    this.bar = value;
                    return this;
                  }

                  @Override
                  public Foo.Builder bag(Object value) {
                    if (value == null) {
                      throw new IllegalArgumentException("'".concat("bag").concat("'").concat(" cannot be null"));
                    }
                    this.bag = value;
                    return this;
                  }

                  @Override
                  public Foo.Builder nullString(String value) {
                    this.nullString = value;
                    return this;
                  }

                  @Override
                  public Foo.Builder bazzes(int value) {
                    this.bazzes = value;
                    return this;
                  }

                  @Override
                  public Foo build() {
                    String $missing = "";
                    if (bar == null) {
                      $missing = $missing.concat(" ").concat("bar");
                    }
                    if (bag == null) {
                      $missing = $missing.concat(" ").concat("bag");
                    }
                    if (!$missing.isEmpty()) {
                      throw new IllegalStateException("Missing required properties:" + $missing);
                    }
                    return new Foo(bar, bag, nullString, bazzes);
                  }
                }
                """.formatted(DateTimeFormatter.ISO_DATE_TIME.format(FORCED_DATE))
            ).toFileObject());
    }
}
