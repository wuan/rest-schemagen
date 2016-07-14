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
    public Stream<RawDataProperty> forType(GenericType<?> genericType) {
            return Arrays.stream(genericType.getDeclaredMethods())
                    .filter(method -> !method.isSynthetic())
                    .filter(method -> method.getDeclaringClass() != Object.class)
                    .filter(method -> method.getReturnType() != void.class)
                    .filter(method -> method.getParameterCount() == 0)
                    .map(this::mapRawDataProperty);
    }

    private RawDataProperty mapRawDataProperty(Method method) {
        return ImmutableRawDataProperty.of(method.getName(), GenericType.of(method), method.getAnnotations(), (Object object) -> valueAccessor(method, object));
    }

    private Object valueAccessor(Method method, Object object) {
        try {
            return method.invoke(object);
        } catch (IllegalAccessException|InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

}
