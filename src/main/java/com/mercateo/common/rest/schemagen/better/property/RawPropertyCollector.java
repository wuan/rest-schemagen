package com.mercateo.common.rest.schemagen.better.property;

import java.util.stream.Stream;

import com.mercateo.common.rest.schemagen.generictype.GenericType;

public interface RawPropertyCollector {
    Stream<RawProperty> forType(GenericType<?> genericType);
}
