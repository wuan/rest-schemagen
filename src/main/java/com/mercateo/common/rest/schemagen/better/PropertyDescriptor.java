package com.mercateo.common.rest.schemagen.better;

import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.internal.TupleStyle;
import org.immutables.value.Value;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.function.Function;

@Value.Immutable
@TupleStyle
public interface PropertyDescriptor {
    GenericType<?> genericType();

    Collection<Property> children();

    Collection<Annotation> annotations();
}
