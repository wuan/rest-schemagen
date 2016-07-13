package com.mercateo.common.rest.schemagen.object.properties;

import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.object.RawDataPropertyCollector;
import com.mercateo.common.rest.schemagen.object.ImmutableRawDataProperty;
import com.mercateo.common.rest.schemagen.object.RawDataProperty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

public class MethodCollector implements RawDataPropertyCollector {

    @Override
    public <T, U> Stream<RawDataProperty<T, U>> forType(GenericType<T> genericType) {
            return Arrays.stream(genericType.getDeclaredMethods())
                    .filter(method -> !method.isSynthetic())
                    .filter(method -> method.getDeclaringClass() != Object.class)
                    .filter(method -> method.getReturnType() != void.class)
                    .filter(method -> method.getParameterCount() == 0)
                    .map(this::mapRawDataProperty);
    }

    private <T, U> RawDataProperty<T, U> mapRawDataProperty(Method method) {
        return ImmutableRawDataProperty.of(method.getName(), GenericType.of(method), method.getAnnotations(), object -> valueAccessor(method, object));
    }

    private <T, U> U valueAccessor(Method method, T object) {
        try {
            //noinspection unchecked
            return (U) method.invoke(object);
        } catch (IllegalAccessException|InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

}
