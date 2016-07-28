package com.mercateo.common.rest.schemagen.better.property;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mercateo.common.rest.schemagen.PropertyType;
import com.mercateo.common.rest.schemagen.PropertyTypeMapper;
import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.generictype.GenericTypeHierarchy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PropertyBuilder {
    private static final String ROOT_NAME = "#";

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

    private static Object rootValueAccessor(Object object) {
        throw new IllegalStateException("cannot call value accessor for root element");
    }

    public Property from(Class<?> propertyClass) {
        return from(GenericType.of(propertyClass));
    }

    public Property from(GenericType<?> genericType) {
        return from(ROOT_NAME, genericType, HashMultimap.create(),
                PropertyBuilder::rootValueAccessor);
    }

    public Property from(String name, GenericType<?> genericType,
                         Multimap<Class<? extends Annotation>, Annotation> annotations, Function valueAccessor) {
        final Map<GenericType<?>, PropertyDescriptor> addedDescriptors = new HashMap<>();
        final Property property = from(name, genericType, annotations, valueAccessor, addedDescriptors);
        knownDescriptors.putAll(addedDescriptors);
        return property;
    }

    private Property from(String name, GenericType<?> genericType, Multimap<Class<? extends Annotation>, Annotation> annotations, Function valueAccessor,
                          Map<GenericType<?>, PropertyDescriptor> addedDescriptors) {
        final PropertyDescriptor propertyDescriptor = getPropertyDescriptor(genericType, addedDescriptors);

        return ImmutableProperty.of(name, propertyDescriptor, valueAccessor, annotationMapBuilder
                .merge(annotations, propertyDescriptor.annotations()));
    }

    private PropertyDescriptor getPropertyDescriptor(GenericType<?> genericType, Map<GenericType<?>, PropertyDescriptor> addedDescriptors) {
        if (knownDescriptors.containsKey(genericType)) {
            return knownDescriptors.get(genericType);
        } else {
            return addedDescriptors.computeIfAbsent(genericType, type -> createPropertyDescriptor(type, addedDescriptors));
        }
    }

    private PropertyDescriptor createPropertyDescriptor(GenericType<?> genericType, Map<GenericType<?>, PropertyDescriptor> addedDescriptors) {
        final PropertyType propertyType = PropertyTypeMapper.of(genericType);

        final List<Property> children;
        switch (propertyType) {
            case OBJECT:
                children = createChildProperties(genericType, addedDescriptors);
                break;

            case ARRAY:
                children = Collections.singletonList(from("", genericType.getContainedType(), HashMultimap.create(), o -> null, addedDescriptors));
                break;

            default:
                children = Collections.emptyList();
                break;
        }

        return ImmutablePropertyDescriptor.of(genericType, children, annotationMapBuilder.createMap(
                genericType.getRawType().getAnnotations()));
    }

    private List<Property> createChildProperties(GenericType<?> genericType, Map<GenericType<?>, PropertyDescriptor> addedDescriptors) {
        return rawPropertyCollectors.stream().flatMap(collector -> genericTypeHierarchy.hierarchy(
                genericType).flatMap(collector::forType)).map(this::mapProperty).collect(Collectors
                .toList());
    }

    private Property mapProperty(RawProperty rawProperty) {
        return from(rawProperty.name(), rawProperty.genericType(), rawProperty.annotations(),
                rawProperty.valueAccessor());
    }

}
