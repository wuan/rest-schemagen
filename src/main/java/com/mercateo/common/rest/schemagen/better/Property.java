package com.mercateo.common.rest.schemagen.better;

import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.internal.TupleStyle;
import org.immutables.value.Value;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Value.Immutable
@TupleStyle
public interface Property {
    String name();

    PropertyDescriptor propertyDescriptor();

    Function valueAccessor();

    default Object getValue(Object object) {
       return valueAccessor().apply(object);
    }

    default Collection<Property> children() {
        return propertyDescriptor().children();
    }

    default GenericType<?> genericType() {
        return propertyDescriptor().genericType();
    }

    Collection<Annotation> annotations();
}
