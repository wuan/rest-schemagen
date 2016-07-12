package com.mercateo.common.rest.schemagen.object;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.mercateo.common.rest.schemagen.PropertyType;
import com.mercateo.common.rest.schemagen.SchemaPropertyContext;
import com.mercateo.common.rest.schemagen.generator.ObjectContext;
import com.mercateo.common.rest.schemagen.generator.PathContext;
import com.mercateo.common.rest.schemagen.generictype.GenericType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class DataObject {

    private final ObjectContext<?> objectContext;
    private final SchemaPropertyContext schemaPropertyContext;
    private final PathContext pathContext;

    public DataObject(GenericType<?> genericType, SchemaPropertyContext schemaPropertyContext) {
        this(ObjectContext.buildFor(genericType).build(), new PathContext(), schemaPropertyContext);
    }

    private DataObject(ObjectContext objectContext, PathContext pathContext, SchemaPropertyContext schemaPropertyContext) {
        this.objectContext = objectContext;
        this.pathContext = pathContext;
        this.schemaPropertyContext = schemaPropertyContext;
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
        if (objectContext.getPropertyType() == PropertyType.OBJECT) {
            ObjectContext currentObjectContext = this.objectContext;
            do {
                for (Field field : objectContext.getGenericType().getDeclaredFields()) {
                    properties.add(getDataPropertyFor(field));
                }
            } while ((currentObjectContext = currentObjectContext.forSuperType()) != null);
        }
        return properties;
    }

    private DataProperty getDataPropertyFor(Field field) {
        final ObjectContext<Object> targetObjectContext = objectContext.forField(field);
        final DataObject targetDataObject = new DataObject(targetObjectContext, pathContext.enter(field.getName(), targetObjectContext.getType()), schemaPropertyContext);
        return ImmutableDataProperty.of(field.getName(), createAnnotationsMap(field.getAnnotations()), targetDataObject, object -> {
            try {
                return field.get(object);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private Map<Class<? extends Annotation>, ? extends Annotation> createAnnotationsMap(Annotation[] annotations) {
        return Arrays.stream(annotations).collect(Collectors.toMap(
            Annotation::annotationType,
            annotation -> annotation));
    }

    public Type getType() {
        return objectContext.getType();
    }
}
