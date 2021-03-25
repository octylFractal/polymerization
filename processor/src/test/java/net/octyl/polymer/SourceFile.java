package net.octyl.polymer;

import com.google.testing.compile.JavaFileObjects;

import javax.tools.JavaFileObject;

public record SourceFile(String fullyQualifiedName, String source) {
    public JavaFileObject toFileObject() {
        return JavaFileObjects.forSourceString(fullyQualifiedName, source);
    }
}
