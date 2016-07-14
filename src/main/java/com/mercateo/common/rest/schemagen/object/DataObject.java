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
import java.util.stream.Stream;

public class DataObject {

    private final GenericType<?> genericType;

    private final PathContext pathContext;

    private final Object defaultValue;

    private final List<?> allowedValues;

    private final PropertyBehaviour propertyBehaviour;

    private final List<RawDataPropertyCollector> rawDataPropertyCollector;

    public DataObject(GenericType<?> genericType, Object defaultValue, List<?> allowedValues, PropertyBehaviour propertyBehaviour) {
        this(genericType, new PathContext(), defaultValue, allowedValues != null ? allowedValues : Collections.emptyList(), propertyBehaviour);
    }

    private DataObject(GenericType<?> genericType, PathContext pathContext, Object defaultValue, List<?> allowedValues, PropertyBehaviour propertyBehaviour) {
        this.genericType = genericType;
        this.pathContext = pathContext;
        this.defaultValue = defaultValue;
        this.allowedValues = allowedValues;
        this.propertyBehaviour = propertyBehaviour;
        this.rawDataPropertyCollector = propertyBehaviour.propertyCollectors();
    }

    public Map<String, DataObject> getChildren() {
        List<DataProperty<?>> properties = getProperties();

        final TreeMap<String, DataObject> children = new TreeMap<>();

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
        return rawDataPropertyCollector.stream()
                .flatMap(collector -> genericType.hierarchy().flatMap(collector::forType))
                .map(this::mapProperty)
                .collect(Collectors.toList());
    }

    private <U> DataProperty<U> mapProperty(RawDataProperty rawDataProperty) {
        final String name = rawDataProperty.name();
        final Annotation[] annotations = rawDataProperty.annotations();
        final GenericType<?> genericType = rawDataProperty.genericType();
        final Function valueAccessor = rawDataProperty.valueAccessor();
        final DataObject dataObject = new DataObject(
                genericType,
                pathContext.enter(rawDataProperty.name(), genericType.getType()),
                defaultValue != null ? valueAccessor.apply(defaultValue) : null,
                allowedValues.stream().map((Object o) -> valueAccessor.apply(o)).filter(Objects::nonNull).collect(Collectors.toList()),
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
