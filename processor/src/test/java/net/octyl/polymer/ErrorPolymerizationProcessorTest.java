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
