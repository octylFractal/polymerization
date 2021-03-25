polymerization
==============
An [AutoValue](https://github.com/google/auto/tree/master/value)-inspired record builder.

# Features
So far, this is a very simple generator:
- Generates an implementation of a builder for a record with little fuss
- Allows annotation a record component with any `@Nullable` (name must match) to allow `null`
- Allows separate files for record and builder

Please make an issue / PR if you'd like something that AutoValue supports, I don't mind working towards feature parity with it.
Completely new features may or may not be added.

# Usage
Simply add the `processor` JAR to your annotation processing toolchain, and the `annotations` library to your compile classpath.

Then, you can write something like:
```java
record Point(int x, int y) {
    public static Builder builder() {
        return new PolymerizeImplPoint_Builder();
    }

    @PolymerizeApi
    interface Builder {
        Builder x(int x);

        Builder y(int y);

        Point build();
    }
}

// And the usage is obvious because the API is already visible
var myPoint = Point.builder().x(5).y(10).build();
```

All auto-generated code is hidden from your API, so it is easy to remove `polymerization` if needed.

Note that if you prefer to keep your builder as a non-nested class, `@PolymerizeApi(result = YourRecordClass.class)`
can be used instead.

# Implementation Details
- Non-nullable components will be null-checked when set in the builder
- Non-nullable components will be required to be set, `IllegalStateException` is thrown if they are not
- Primitives count as non-nullable components, internally stored as wrappers
