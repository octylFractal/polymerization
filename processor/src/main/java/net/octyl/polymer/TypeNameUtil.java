package net.octyl.polymer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

public class TypeNameUtil {
    public static ClassName rawType(TypeName typeName) {
        if (typeName instanceof ClassName className) {
            return className;
        } else if (typeName instanceof ParameterizedTypeName parameterizedTypeName) {
            return parameterizedTypeName.rawType;
        }
        throw new IllegalArgumentException("Not able to create a raw type from " +
            "'" + typeName + "' (" + typeName.getClass() + ")");
    }

    private TypeNameUtil() {
    }
}
