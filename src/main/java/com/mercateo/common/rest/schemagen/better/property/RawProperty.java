package com.mercateo.common.rest.schemagen.better.property;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Multimap;
import org.immutables.value.Value;

import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.internal.TupleStyle;

@Value.Immutable
@TupleStyle
public interface RawProperty {
    String name();

    GenericType<?> genericType();

    Multimap<Class<? extends Annotation>, Annotation> annotations();

    Function valueAccessor();
}
