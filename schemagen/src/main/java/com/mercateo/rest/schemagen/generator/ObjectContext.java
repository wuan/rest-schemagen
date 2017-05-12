package com.mercateo.rest.schemagen.generator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.gentyref.GenericTypeReflector;
import com.mercateo.immutables.DataClass;
import com.mercateo.rest.schemagen.IgnoreInRestSchema;
import com.mercateo.rest.schemagen.SchemaPropertyContext;
import com.mercateo.rest.schemagen.constraints.SizeConstraints;
import com.mercateo.rest.schemagen.constraints.ValueConstraints;
import com.mercateo.rest.schemagen.generictype.GenericClass;
import com.mercateo.rest.schemagen.generictype.GenericType;
import com.mercateo.rest.schemagen.plugin.IndividualSchemaGenerator;
import com.mercateo.rest.schemagen.plugin.PropertySchema;
import com.mercateo.rest.schemagen.property.PropertySubType;
import com.mercateo.rest.schemagen.property.PropertySubTypeMapper;
import com.mercateo.rest.schemagen.property.PropertyType;
import com.mercateo.rest.schemagen.property.PropertyTypeMapper;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import javax.validation.Constraint;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.ws.rs.PathParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Value.Immutable
@DataClass
public abstract class ObjectContext<T> {

    private static final Set<Class<? extends Annotation>> IGNORE_ANNOTATIONS = new HashSet<>(Arrays
            .asList(JsonIgnore.class, IgnoreInRestSchema.class));

    public static <T> ObjectContextBuilder<T> buildFor(Type type, Class<T> clazz) {
        return buildFor(GenericType.of(type, clazz));
    }

    public static <T> ObjectContextBuilder<T> buildFor(Class<T> clazz) {
        return buildFor(new GenericClass<>(clazz));
    }

    public static <T> ObjectContextBuilder<T> buildFor(GenericType<T> genericType) {
        PropertyType propertyType = PropertyTypeMapper.of(genericType);
        return new ObjectContextBuilder<T>()
                .withGenericType(genericType)
                .withPropertyType(propertyType)
                .withPropertySubType(PropertySubTypeMapper.of(genericType, propertyType));
    }

    private static <U, T extends U> ObjectContext<U> buildObjectContextForSuper(
            GenericType<U> superType, List<T> allowedValues, T defaultValue) {

        return ObjectContext.buildFor(superType)
                .withDefaultValue(defaultValue)
                .withAllowedValues(allowedValues != null ? allowedValues : Collections.emptyList())
                .build();
    }

    public abstract GenericType<T> getGenericType();

    @Nullable
    public abstract T getDefaultValue();

    @Nullable
    public abstract T getCurrentValue();

    public abstract List<T> getAllowedValues();

    @Value.Default
    public boolean isRequired() {
        return false;
    }

    @Value.Default
    public SizeConstraints getSizeConstraints() {
        return SizeConstraints.empty();
    }

    @Value.Default
    public ValueConstraints getValueConstraints() {
        return ValueConstraints.empty();
    }

    public abstract PropertyType getPropertyType();

    @Value.Default
    public PropertySubType getPropertySubType() {
        return PropertySubType.NONE;
    }

    @Nullable
    public abstract Class<? extends IndividualSchemaGenerator> getSchemaGenerator();

    public ObjectContext<?> forSuperType() {
        GenericType<? super T> superType = getGenericType().getSuperType();
        if (superType != null) {
            return buildObjectContextForSuper(superType, getAllowedValues(), getDefaultValue());
        } else {
            return null;
        }
    }

    public ObjectContext<?> getContained() {
        final GenericType<?> containedType = getGenericType().getContainedType();
        return ObjectContext.buildFor(containedType).build();
    }

    @SuppressWarnings("unchecked")
    public <U> ObjectContext<U> forField(Field field) {
        final GenericType<U> fieldType = GenericType.of(GenericTypeReflector.getExactFieldType(
                field, getType()), (Class<U>) field.getType());
        final ObjectContextBuilder<U> builder = ObjectContext.buildFor(fieldType);

        T defaultValue = getDefaultValue();
        if (defaultValue != null) {
            builder.withDefaultValue(getFieldValue(field, defaultValue));
        }

        List<T> allowedValues = getAllowedValues();
        if (allowedValues != null && !fieldType.getRawType().isPrimitive()) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            builder.withAllowedValues(allowedValues.stream()
                    .filter(Objects::nonNull)
                    .flatMap(value -> (Stream<U>) addToAllowedValues(field, value))
                    .collect(Collectors.toList())
            );
        }

        if (isRequired(field)) {
            builder.withIsRequired(true);
        }

        determineConstraints(Size.class, field, SizeConstraints::new)
                .ifPresent(builder::withSizeConstraints);

        builder.withValueConstraints(new ValueConstraints(
                determineConstraints(Max.class, field, Max::value),
                determineConstraints(Min.class, field, Min::value)));

        final PropertySchema schemaGenerator = field.getAnnotation(PropertySchema.class);
        if (schemaGenerator != null) {
            builder.withSchemaGenerator(schemaGenerator.schemaGenerator());
        }

        return builder.build();
    }

    private boolean isRequired(Field field) {
        if (field.isAnnotationPresent(NotNull.class)) {
            return true;
        }
        Annotation[] annotations = field.getAnnotations();
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType.isAnnotationPresent(Constraint.class) && annotationType
                    .isAnnotationPresent(NotNull.class)) {
                return true;
            }
        }
        return false;
    }

    private <U, C extends Annotation> Optional<U> determineConstraints(Class<C> clazz, Field field, Function<C, U> callback) {
        C constraint = field.getAnnotation(clazz);
        if (constraint != null) {
            return Optional.of(callback.apply(constraint));
        }
        for (Annotation annotation : field.getAnnotations()) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType.isAnnotationPresent(Constraint.class) && annotationType.isAnnotationPresent(clazz)) {
                return Optional.of(callback.apply(annotationType.getAnnotation(clazz)));
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    private <U> Stream<U> addToAllowedValues(Field field, T object) {
        U fieldValue = getFieldValue(field, object);
        return fieldValue != null ? Stream.of(fieldValue) : Stream.<U>empty();
    }

    public boolean isApplicable(Field field, SchemaPropertyContext context) {
        return !Modifier.isStatic(field.getModifiers()) && //
                !field.isSynthetic() && //
                !IGNORE_ANNOTATIONS.stream().anyMatch(a -> field.getAnnotation(a) != null) && //
                isApplicableFor(field, context) && //
                isApplicableForPathParam(field);
    }

    private boolean isApplicableForPathParam(Field field) {
        PathParam pathParamAnnotation = field.getAnnotation(PathParam.class);
        if (pathParamAnnotation == null) {
            return true;
        }

        T currentValue = getCurrentValue();

        return currentValue != null && getFieldValue(field, currentValue) == null;

    }

    private boolean isApplicableFor(Field field, SchemaPropertyContext context) {
        // return field.getDeclaringClass().equals(ObjectWithSchema.class)
        return context.isFieldApplicable(field);
    }

    public Class<?> getRawType() {
        return getGenericType().getRawType();
    }

    public Type getType() {
        return getGenericType().getType();
    }

    private <U> U getFieldValue(Field field, T object) {
        try {
            field.setAccessible(true);
            //noinspection unchecked
            return (U) field.get(object);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
