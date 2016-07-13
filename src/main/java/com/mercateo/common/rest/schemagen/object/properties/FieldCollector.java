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
    public <T, U> Stream<RawDataProperty<T, U>> forType(GenericType<T> genericType) {
        return Arrays.stream(genericType.getDeclaredFields())
                .filter(field -> !field.isSynthetic())
                .map(this::mapRawDataProperty);
    }

    private <T, U> RawDataProperty<T, U> mapRawDataProperty(Field field) {
        return ImmutableRawDataProperty.of(field.getName(), GenericType.of(field), field.getAnnotations(), object -> valueAccessor(field, object));
    }

    private <T, U> U valueAccessor(Field field, T object) {
        try {
            //noinspection unchecked
            return (U) field.get(object);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
