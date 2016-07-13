package com.mercateo.common.rest.schemagen.object;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class DefaultSchemaConfiguration extends SchemaConfiguration {
    @Override
    public boolean enableFields() {
        return true;
    }

    @Override
    public List<Predicate<Field>> fieldCheckers() {
        return Arrays.asList(
                field -> !field.isSynthetic()
        );
    }

    @Override
    public boolean enableMethods() {
        return true;
    }

    @Override
    public List<Predicate<Method>> methodCheckers() {
        return Arrays.asList(
                method -> !method.isSynthetic(),
                method -> method.getParameterCount() == 0,
                method -> method.getReturnType() != void.class,
                method -> method.getDeclaringClass() == Object.class
        );
    }

    @Override
    public List<Predicate<DataObject>> propertyCheckers() {
        return null;
    }

    @Override
    public Predicate<DataObject> propertyChecker() {
        return dataObject -> true;
    }
}
