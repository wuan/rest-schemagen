package com.mercateo.common.rest.schemagen.better.property;

import com.google.common.collect.Multimap;
import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.internal.TupleStyle;
import org.immutables.value.Value;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Value.Immutable
@TupleStyle
public interface PropertyDescriptor {
    GenericType<?> genericType();

    Collection<Property> children();

    Multimap<Class<? extends Annotation>, Annotation> annotations();
}
