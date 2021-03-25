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
