package com.mercateo.common.rest.schemagen.better;

import com.googlecode.gentyref.GenericTypeReflector;
import com.mercateo.common.rest.schemagen.generictype.GenericType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PropertyBuilder {

    ConcurrentHashMap<GenericType<?>, PropertyDescriptor> map = new ConcurrentHashMap<>();

    public Property from(Class<?> propertyClass) {
        return from(GenericType.of(propertyClass));
    }

    public Property from(Class<?> propertyClass, Type propertyType) {
        return from(GenericType.of(propertyType, propertyClass));
    }

    public Property from(GenericType<?> genericType) {
        return from("#", genericType, Collections.emptyList(), object -> {throw new IllegalStateException("cannot call value accessor for root element");});
    }

    public Property from(String name, GenericType<?> genericType, List<Annotation> annotations, Function valueAccessor) {
        final List<Property> children = new ArrayList<>();
        final Field[] declaredFields = genericType.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            final Property child = createProperty(GenericType.of(declaredField, genericType.getType()), declaredField, declaredField.getName());
            children.add(child);
        }

        final List<Annotation> classAnnotations = Arrays.asList(genericType.getRawType().getAnnotations());
        final PropertyDescriptor propertyDescriptor = ImmutablePropertyDescriptor.of(genericType, children, classAnnotations);

        Collection<Annotation> propertyAnnotations = new ArrayList<>(annotations);
        propertyAnnotations.addAll(classAnnotations);

        return ImmutableProperty.of(name, propertyDescriptor, valueAccessor, propertyAnnotations);
    }

    private Property createProperty(GenericType genericType, Field declaredField, String childName) {
        final List<Annotation> childAnnotations = Arrays.asList(declaredField.getAnnotations());
        return from(childName, genericType, childAnnotations, object -> {
            try {
                return declaredField.get(object);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        });
    }
}
