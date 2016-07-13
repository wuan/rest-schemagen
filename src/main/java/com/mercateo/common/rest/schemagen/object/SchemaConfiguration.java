package com.mercateo.common.rest.schemagen.object;

import com.mercateo.common.rest.schemagen.internal.DataClassStyle;
import com.mercateo.common.rest.schemagen.internal.TupleStyle;
import org.immutables.value.Value;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Predicate;

@Value.Immutable
@DataClassStyle
public abstract class SchemaConfiguration {

    public static final SchemaConfiguration FIELDS_ENABLED = SchemaConfiguration.builder()
            .withEnableFields(true)
            .addFieldCheckers(field -> !field.isSynthetic())
            .build();

    public static final SchemaConfiguration METHODS_ENABLED = SchemaConfiguration.builder()
            .withEnableMethods(true)
            .addMethodCheckers(method -> !method.isSynthetic())
            .addMethodCheckers(method -> method.getDeclaringClass() != Object.class)
            .addMethodCheckers(method -> method.getReturnType() != void.class)
            .addMethodCheckers(method -> method.getParameterCount() == 0)
            .build();

    @Value.Default
    public boolean enableFields() {
        return false;
    }

    public abstract List<Predicate<Field>> fieldCheckers();

    @Value.Lazy
    public Predicate<Field> fieldChecker() {
        return fieldCheckers().stream().reduce(field -> true, Predicate::and);
    }

    @Value.Default
    public boolean enableMethods() {
        return false;
    }

    public abstract List<Predicate<Method>> methodCheckers();

    @Value.Lazy
    public Predicate<Method> methodChecker() {
        return methodCheckers().stream().reduce(method -> true, Predicate::and);
    }

    public abstract List<Predicate<DataObject>> propertyCheckers();

    @Value.Lazy
    public Predicate<DataObject> propertyChecker() {
        return propertyCheckers().stream().reduce(method -> true, Predicate::and);
    }

    public static SchemaConfigurationBuilder builder() {
        return new SchemaConfigurationBuilder();
    }
}
