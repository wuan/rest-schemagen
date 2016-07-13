package com.mercateo.common.rest.schemagen.object;

import com.mercateo.common.rest.schemagen.generictype.GenericType;

import java.util.stream.Stream;

public interface RawDataPropertyCollector<T> {
    <T, U> Stream<RawDataProperty<T, U>> forType(GenericType<? super T> genericType);
}
