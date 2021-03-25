package net.octyl.polymer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark an interface as a builder for a record. By default, it is assumed
 * that the record class is the containing class, but this can be changed
 * via {@link #result()}.
 *
 * <p>
 * The annotation processor will generate a class with the following name:
 * {@code "%s.PolymerizeImpl%s".formatted(packageName, runtimeClassName.replace('$', '_'))}
 * That is, inner classes are denoted by {@code _}, and all prefixed with
 * {@code PolymerizeImpl}, to mirror the name of this annotation.
 * </p>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface PolymerizeApi {
    /**
     * {@return the record class that this builder results in} The default value of
     * {@code Void.class} indicates that it should be the containing class.
     */
    Class<?> result() default Void.class;
}
