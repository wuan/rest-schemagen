package com.mercateo.common.rest.schemagen.object;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.mercateo.common.rest.schemagen.SchemaPropertyContext;
import com.mercateo.common.rest.schemagen.generator.ObjectContext;
import com.mercateo.common.rest.schemagen.generator.PathContext;
import com.mercateo.common.rest.schemagen.generictype.GenericType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class DataObject {

    private final ObjectContext<?> objectContext;
    private final SchemaPropertyContext schemaPropertyContext;
    private final PathContext pathContext;

    public DataObject(GenericType<?> genericType, SchemaPropertyContext schemaPropertyContext) {
        this.schemaPropertyContext = schemaPropertyContext;
        objectContext = ObjectContext.buildFor(genericType).build();
        pathContext = new PathContext();
    }

    public Map<String, DataObject> getChildren() {
        List<DataProperty> properties = getProperties();

        final TreeMap<String, DataObject> children = new TreeMap<>();

        for (DataProperty property : properties) {
            final DataObject dataObject = property.object();
            if (property.annotations().containsKey(JsonUnwrapped.class)) {
                children.putAll(dataObject.getChildren());
            } else {
                children.put(property.name(), dataObject);
            }
        }

        return children;
    }

    private List<DataProperty> getProperties() {
        List<DataProperty> properties = new ArrayList<>();
        ObjectContext currentObjectContext = this.objectContext;
        do {
            final Class<?> rawType = objectContext.getGenericType().getRawType();
            for (Field field : objectContext.getGenericType().getDeclaredFields()) {
                properties.add(ImmutableDataProperty.of(field.getName(), createAnnotationsMap(field.getAnnotations()), null));
            }
        } while ((currentObjectContext = currentObjectContext.forSuperType()) != null);
        return properties;
    }

    private Map<Class<? extends Annotation>, ? extends Annotation> createAnnotationsMap(Annotation[] annotations) {
            Arrays.stream(annotations).collect(Collectors.toMap())
        }
    }
