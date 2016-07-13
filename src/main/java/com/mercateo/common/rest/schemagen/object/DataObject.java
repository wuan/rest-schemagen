package com.mercateo.common.rest.schemagen.object;

import com.mercateo.common.rest.schemagen.PropertyType;
import com.mercateo.common.rest.schemagen.PropertyTypeMapper;
import com.mercateo.common.rest.schemagen.generator.PathContext;
import com.mercateo.common.rest.schemagen.generictype.GenericType;

import java.lang.annotation.Annotation;
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

public class DataObject<T> {

    private final GenericType<T> genericType;

    private final PathContext pathContext;

    private final T defaultValue;

    private final List<T> allowedValues;

    private final PropertyBehaviour propertyBehaviour;

    private final RawDataPropertyCollector<T> rawDataPropertyCollector;

    public DataObject(GenericType<T> genericType, T defaultValue, List<T> allowedValues, PropertyBehaviour propertyBehaviour) {
        this(genericType, new PathContext(), defaultValue, allowedValues != null ? allowedValues : Collections.emptyList(), propertyBehaviour);
    }

    private DataObject(GenericType<T> genericType, PathContext pathContext, T defaultValue, List<T> allowedValues, PropertyBehaviour propertyBehaviour) {
        this.genericType = genericType;
        this.pathContext = pathContext;
        this.defaultValue = defaultValue;
        this.allowedValues = allowedValues;
        this.propertyBehaviour = propertyBehaviour;
        this.rawDataPropertyCollector = null;
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
            properties.addAll(createProperties());
        }
        return properties;
    }

    private List<DataProperty<?>> createProperties() {
        return genericType.hierarchy().flatMap(rawDataPropertyCollector::forType).map(this::mapProperty).collect(Collectors.toList());
    }

    private <U> DataProperty<U> mapProperty(RawDataProperty<T, U> rawDataProperty) {
        final String name = rawDataProperty.name();
        final Annotation[] annotations = rawDataProperty.annotations();
        final GenericType<U> genericType = rawDataProperty.genericType();
        final Function<T, U> valueAccessor = rawDataProperty.valueAccessor();
        final DataObject<U> dataObject = new DataObject<>(
                genericType,
                pathContext.enter(rawDataProperty.name(), genericType.getType()),
                defaultValue != null ? valueAccessor.apply(defaultValue) : null,
                allowedValues.stream().map(valueAccessor).filter(Objects::nonNull).collect(Collectors.toList()),
                propertyBehaviour
        );

        return ImmutableDataProperty.of(name, createAnnotationsMap(annotations), dataObject);
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
