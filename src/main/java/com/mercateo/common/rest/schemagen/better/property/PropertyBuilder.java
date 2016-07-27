package com.mercateo.common.rest.schemagen.better.property;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mercateo.common.rest.schemagen.PropertyType;
import com.mercateo.common.rest.schemagen.PropertyTypeMapper;
import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.generictype.GenericTypeHierarchy;

public class PropertyBuilder {
    public static final String ROOT_NAME = "#";

    private final GenericTypeHierarchy genericTypeHierarchy;

    private final List<RawPropertyCollector> rawPropertyCollectors;

    private final AnnotationMapBuilder annotationMapBuilder;

    private ConcurrentHashMap<GenericType<?>, PropertyDescriptor> knownDescriptors;

    public PropertyBuilder(List<RawPropertyCollector> rawPropertyCollectors) {
        this.rawPropertyCollectors = rawPropertyCollectors;
        this.genericTypeHierarchy = new GenericTypeHierarchy();
        this.annotationMapBuilder = new AnnotationMapBuilder();
        this.knownDescriptors = new ConcurrentHashMap<>();
    }

    public static Object rootValueAccessor(Object object) {
        throw new IllegalStateException("cannot call value accessor for root element");
    }

    public Property from(Class<?> propertyClass) {
        return from(GenericType.of(propertyClass));
    }

    public Property from(Class<?> propertyClass, Type propertyType) {
        return from(GenericType.of(propertyType, propertyClass));
    }

    public Property from(GenericType<?> genericType) {
        return from(ROOT_NAME, genericType, HashMultimap.create(),
                PropertyBuilder::rootValueAccessor);
    }

    public Property from(String name, GenericType<?> genericType,
            Multimap<Class<? extends Annotation>, Annotation> annotations, Function valueAccessor) {
        final PropertyDescriptor propertyDescriptor = getPropertyDescriptor(genericType);

        return ImmutableProperty.of(name, propertyDescriptor, valueAccessor, annotationMapBuilder
                .merge(annotations, propertyDescriptor.annotations()));
    }

    private PropertyDescriptor getPropertyDescriptor(GenericType<?> genericType) {
        return knownDescriptors.computeIfAbsent(genericType, this::createPropertyDescriptor);
    }

    private PropertyDescriptor createPropertyDescriptor(GenericType<?> genericType) {
        final PropertyType propertyType = PropertyTypeMapper.of(genericType);
        final List<Property> children = propertyType == PropertyType.OBJECT ? createChildProperties(
                genericType) : Collections.emptyList();

        return ImmutablePropertyDescriptor.of(genericType, children, annotationMapBuilder.createMap(
                genericType.getRawType().getAnnotations()));
    }

    private List<Property> createChildProperties(GenericType<?> genericType) {
        return rawPropertyCollectors.stream().flatMap(collector -> genericTypeHierarchy.hierarchy(
                genericType).flatMap(collector::forType)).map(this::mapProperty).collect(Collectors
                        .toList());
    }

    private Property mapProperty(RawProperty rawProperty) {
        return from(rawProperty.name(), rawProperty.genericType(), rawProperty.annotations(),
                rawProperty.valueAccessor());
    }
}
