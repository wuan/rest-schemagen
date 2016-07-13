package com.mercateo.common.rest.schemagen.object;

import com.mercateo.common.rest.schemagen.PropertyType;
import com.mercateo.common.rest.schemagen.PropertyTypeMapper;
import com.mercateo.common.rest.schemagen.generator.PathContext;
import com.mercateo.common.rest.schemagen.generictype.GenericType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataObject <T> {

    private final GenericType<T> genericType;

    private final PathContext pathContext;

    private final T defaultValue;

    private final List<T> allowedValues;

    private final SchemaConfiguration schemaConfiguration;

    public DataObject(GenericType<T> genericType, T defaultValue, List<T> allowedValues, SchemaConfiguration schemaConfiguration) {
        this(genericType, new PathContext(), defaultValue, allowedValues != null ? allowedValues : Collections.emptyList(), schemaConfiguration);
    }

    private DataObject(GenericType<T> genericType, PathContext pathContext, T defaultValue, List<T> allowedValues, SchemaConfiguration schemaConfiguration) {
        this.genericType = genericType;
        this.pathContext = pathContext;
        this.defaultValue = defaultValue;
        this.allowedValues = allowedValues;
        this.schemaConfiguration = schemaConfiguration;
    }

    public Map<String, DataObject<?>> getChildren() {
        List<DataProperty<?>> properties = getProperties();

        final TreeMap<String, DataObject<?>> children = new TreeMap<>();

        for (DataProperty property : properties) {
            final DataObject dataObject = property.object();
            children.put(property.name(), dataObject);
        }

        return children;
    }

    private List<DataProperty<?>> getProperties() {
        List<DataProperty<?>> properties = new ArrayList<>();
        if (PropertyTypeMapper.of(genericType) == PropertyType.OBJECT) {
            addProperties(properties);
        }
        return properties;
    }

    private void addProperties(List<DataProperty<?>> properties) {
        GenericType genericType = this.genericType;
        do {
            addFields(properties, genericType);
            addGetters(properties, genericType);
        } while ((genericType = genericType.getSuperType()) != null);
    }

    private void addFields(List<DataProperty<?>> properties, GenericType genericType) {
        if (schemaConfiguration.enableFields()) {
            Arrays.stream(genericType.getDeclaredFields())
                    .filter(schemaConfiguration.fieldChecker())
                    .map(this::getDataPropertyFor)
                    .forEach(properties::add);
        }
    }

    private DataProperty getDataPropertyFor(Field field) {
        return getDataProperty(field.getName(), GenericType.of(field), field.getAnnotations(), o -> valueAccessor(field, o));
    }

    private <U> U valueAccessor(Field field, T object) {
        try {
            //noinspection unchecked
            return (U) field.get(object);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private void addGetters(List<DataProperty<?>> properties, GenericType genericType) {
        if (schemaConfiguration.enableMethods()) {
            Arrays.stream(genericType.getDeclaredMethods())
                    .filter(schemaConfiguration.methodChecker())
                    .map(this::getDataPropertyFor)
                    .forEach(properties::add);
        }
    }

    private DataProperty getDataPropertyFor(Method method) {
        return getDataProperty(method.getName(), GenericType.of(method), method.getAnnotations(), o -> valueAccessor(method, o));
    }

    private <U> U valueAccessor(Method method, T object) {
        try {
            //noinspection unchecked
            return (U) method.invoke(object);
        } catch (IllegalAccessException|InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private <U> DataProperty<U> getDataProperty(String name, GenericType<U> genericType, Annotation[] annotations, Function<T, U> valueAccessor) {
        final DataObject<U> dataObject = new DataObject<>(
                genericType,
                pathContext.enter(name, genericType.getType()),
                defaultValue != null ? valueAccessor.apply(defaultValue) : null,
                allowedValues.stream().map(valueAccessor).filter(Objects::nonNull).collect(Collectors.toList()),
                schemaConfiguration
        );

        return ImmutableDataProperty.of(name, createAnnotationsMap(annotations), dataObject, valueAccessor);
    }

    private Map<Class<? extends Annotation>, ? extends Annotation> createAnnotationsMap(Annotation[] annotations) {
        return Arrays.stream(annotations).collect(Collectors.toMap(
            Annotation::annotationType,
            annotation -> annotation));
    }

    public Type getType() {
        return genericType.getType();
    }
}
