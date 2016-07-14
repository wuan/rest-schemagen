package com.mercateo.common.rest.schemagen.generictype;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class GenericType<T> {

    private final Class<T> rawType;

    GenericType(Class<T> rawType) {
        this.rawType = requireNonNull(rawType);
    }

    public final Class<T> getRawType() {
        return rawType;
    }

    public abstract String getSimpleName();

    public abstract Type getType();

    public abstract GenericType<?> getContainedType();

    public boolean isInstanceOf(Class<?> clazz) {
        return requireNonNull(clazz).isAssignableFrom(getRawType());
    }

    public boolean isIterable() {
        return Iterable.class.isAssignableFrom(getRawType());
    }

    public static <T> GenericType<T> of(Class<T> type) {
        return of(type, null);
    }

    public static GenericType<?> of(Type type) {
        return of(type, null);
    }

    @SuppressWarnings("unchecked")
	public static <T> GenericType<T> of(Type type, Class<T> rawType) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return new GenericParameterizedType<>(parameterizedType, (Class<T>) parameterizedType
                    .getRawType());
        } else if (type instanceof Class) {
            return new GenericClass<>((Class<T>) type);
        } else if (type instanceof GenericArrayType) {
            return new GenericArray<>((GenericArrayType) type, requireNonNull(rawType));
        }
        {
            throw new IllegalStateException("unhandled type " + type);
        }
    }

    public Field[] getDeclaredFields() {
        return getRawType().getDeclaredFields();
    }

    public Method[] getDeclaredMethods() {
        return getRawType().getDeclaredMethods();
    }

    public abstract GenericType<? super T> getSuperType();

    public static GenericType<?> of(Field field) {
        return of(field.getGenericType(), field.getType());
    }

    public static GenericType<?> of(Method method) {
        return of(method.getGenericReturnType(), method.getReturnType());
    }

    public Stream<GenericType<?>> hierarchy() {

        Iterable<GenericType<?>> resultIterable = new GenericTypeIterable(this);

        return StreamSupport.stream(resultIterable.spliterator(), false);

    }
    private static class GenericTypeIterable implements Iterable<GenericType<?>> {

        private final GenericType<?> genericType;

        GenericTypeIterable(GenericType<?> genericType) {
            this.genericType = genericType;
        }

        @Override
        public Iterator<GenericType<?>> iterator() {
            return new GenericTypeIterator(genericType);
        }
    }

    private static class GenericTypeIterator implements Iterator<GenericType<?>> {
        GenericType<?> currentType;

        GenericTypeIterator(GenericType<?> genericType) {
            this.currentType = genericType;
        }

        @Override
        public boolean hasNext() {
            return currentType != null;
        }

        @Override
        public GenericType<?> next() {
            GenericType<?> type = currentType;
            currentType = currentType.getSuperType();
            return type;
        }

    }
}
