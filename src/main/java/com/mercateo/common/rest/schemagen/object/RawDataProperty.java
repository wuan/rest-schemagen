package com.mercateo.common.rest.schemagen.object;

import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.internal.TupleStyle;
import org.immutables.value.Value;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.function.Function;

@Value.Immutable
@TupleStyle
public interface RawDataProperty {
    String name();

    GenericType<?> genericType();

    Annotation[] annotations();

    Function<?, ?> valueAccessor();
}
