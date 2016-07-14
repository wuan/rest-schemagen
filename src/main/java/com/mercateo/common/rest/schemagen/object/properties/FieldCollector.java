package com.mercateo.common.rest.schemagen.object.properties;

import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.object.RawDataPropertyCollector;
import com.mercateo.common.rest.schemagen.object.ImmutableRawDataProperty;
import com.mercateo.common.rest.schemagen.object.RawDataProperty;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Stream;

public class FieldCollector implements RawDataPropertyCollector {

    @Override
    public Stream<RawDataProperty> forType(GenericType<?> genericType) {
        return Arrays.stream(genericType.getDeclaredFields())
                .filter(field -> !field.isSynthetic())
                .map(this::mapRawDataProperty);
    }

    private RawDataProperty mapRawDataProperty(Field field) {
        return ImmutableRawDataProperty.of(field.getName(), GenericType.of(field), field.getAnnotations(), (Object object) -> valueAccessor(field, object));
    }

    private Object valueAccessor(Field field, Object object) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
