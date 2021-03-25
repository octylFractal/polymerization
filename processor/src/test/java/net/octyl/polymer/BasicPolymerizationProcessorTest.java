/*
 * Copyright (c) Octavia Togami <https://octyl.net>
 * Copyright (c) contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.octyl.polymer;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;


import javax.tools.JavaFileObject;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.google.testing.compile.JavaFileObjects;
import org.junit.jupiter.api.Test;

public class BasicPolymerizationProcessorTest {
    private static final ZonedDateTime FORCED_DATE =
        ZonedDateTime.of(2021, 3, 24, 0, 0, 0, 0, ZoneOffset.UTC);

    private static final JavaFileObject TEST_NULLABLE = JavaFileObjects.forSourceString(
        "test.Nullable",
        // language=java
        """
        package test;
        public @interface Nullable {}
        """
    );

    @Test
    void basicRecord() {
        var source = JavaFileObjects.forSourceString(
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
            .compile(source, TEST_NULLABLE);

        assertThat(compilation).succeededWithoutWarnings();

        assertThat(compilation).generatedSourceFile("PolymerizeImplFoo_Builder")
            .hasSourceEquivalentTo(JavaFileObjects.forSourceString(
                "PolymerizeImplFoo_Builder",
                // language=java
                """
                import java.lang.IllegalArgumentException;
                import java.lang.IllegalStateException;
                import java.lang.Integer;
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
                  private Integer bazzes;

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
                    if (bazzes == null) {
                      $missing = $missing.concat(" ").concat("bazzes");
                    }
                    if (!$missing.isEmpty()) {
                      throw new IllegalStateException("Missing required properties:" + $missing);
                    }
                    return new Foo(bar, bag, nullString, bazzes);
                  }
                }
                """.formatted(DateTimeFormatter.ISO_DATE_TIME.format(FORCED_DATE))
            ));
    }

    @Test
    void separateBuilderFile() {
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
            @PolymerizeApi(result = Foo.class)
            interface FooBuilder {
                FooBuilder bar(String value);

                Foo build();
            }
            """
        );
        var compilation = javac()
            .withProcessors(new PolymerizationProcessor(FORCED_DATE))
            .compile(source, sourceBuilder);

        assertThat(compilation).succeededWithoutWarnings();

        assertThat(compilation).generatedSourceFile("PolymerizeImplFooBuilder")
            .hasSourceEquivalentTo(JavaFileObjects.forSourceString(
                "PolymerizeImplFooBuilder",
                // language=java
                """
                import java.lang.IllegalArgumentException;
                import java.lang.IllegalStateException;
                import java.lang.Override;
                import java.lang.String;
                import javax.annotation.processing.Generated;

                @Generated(
                    value = "net.octyl.polymer.PolymerizationProcessor",
                    date = "%s"
                )
                final class PolymerizeImplFooBuilder implements FooBuilder {
                  private String bar;

                  @Override
                  public FooBuilder bar(String value) {
                    if (value == null) {
                      throw new IllegalArgumentException("'".concat("bar").concat("'").concat(" cannot be null"));
                    }
                    this.bar = value;
                    return this;
                  }

                  @Override
                  public Foo build() {
                    String $missing = "";
                    if (bar == null) {
                      $missing = $missing.concat(" ").concat("bar");
                    }
                    if (!$missing.isEmpty()) {
                      throw new IllegalStateException("Missing required properties:" + $missing);
                    }
                    return new Foo(bar);
                  }
                }
                """.formatted(DateTimeFormatter.ISO_DATE_TIME.format(FORCED_DATE))
            ));
    }

    @Test
    void anyNameIsOkay() {
        var source = JavaFileObjects.forSourceString(
            "Foo",
            // language=java
            """
            import net.octyl.polymer.PolymerizeApi;
            record Foo(String bar) {
                @PolymerizeApi
                interface MassivelyFunctionalCollatingPropertiesCombinerForFoo {
                    MassivelyFunctionalCollatingPropertiesCombinerForFoo bar(String value);

                    Foo finishCollatingAllThePropertiesInAFunctioningWay();
                }
            }
            """
        );
        var compilation = javac()
            .withProcessors(new PolymerizationProcessor(FORCED_DATE))
            .compile(source);

        assertThat(compilation).succeededWithoutWarnings();

        assertThat(compilation).generatedSourceFile("PolymerizeImplFoo_MassivelyFunctionalCollatingPropertiesCombinerForFoo")
            .hasSourceEquivalentTo(JavaFileObjects.forSourceString(
                "PolymerizeImplFoo_Builder",
                // language=java
                """
                import java.lang.IllegalArgumentException;
                import java.lang.IllegalStateException;
                import java.lang.Override;
                import java.lang.String;
                import javax.annotation.processing.Generated;

                @Generated(
                    value = "net.octyl.polymer.PolymerizationProcessor",
                    date = "%s"
                )
                final class PolymerizeImplFoo_MassivelyFunctionalCollatingPropertiesCombinerForFoo implements Foo.MassivelyFunctionalCollatingPropertiesCombinerForFoo {
                  private String bar;

                  @Override
                  public Foo.MassivelyFunctionalCollatingPropertiesCombinerForFoo bar(String value) {
                    if (value == null) {
                      throw new IllegalArgumentException("'".concat("bar").concat("'").concat(" cannot be null"));
                    }
                    this.bar = value;
                    return this;
                  }

                  @Override
                  public Foo finishCollatingAllThePropertiesInAFunctioningWay() {
                    String $missing = "";
                    if (bar == null) {
                      $missing = $missing.concat(" ").concat("bar");
                    }
                    if (!$missing.isEmpty()) {
                      throw new IllegalStateException("Missing required properties:" + $missing);
                    }
                    return new Foo(bar);
                  }
                }
                """.formatted(DateTimeFormatter.ISO_DATE_TIME.format(FORCED_DATE))
            ));
    }
}
