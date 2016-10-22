package com.oroplatform.idea.oroplatform;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Functions {
    private Functions(){}

    public static <T> Stream<T> toStream(Optional<T> optional) {
        return optional.map(Arrays::asList)
            .orElseGet(Collections::emptyList)
            .stream();
    }

    public static <T> Stream<T> toStream(Supplier<T> supplier) {
        return toStream(Optional.ofNullable(supplier.get()));
    }
}
