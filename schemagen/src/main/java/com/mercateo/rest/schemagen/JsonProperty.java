package com.mercateo.rest.schemagen;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.mercateo.rest.schemagen.plugin.IndividualSchemaGenerator;
import com.mercateo.rest.schemagen.property.PropertyType;
import com.mercateo.rest.schemagen.property.PropertyTypeMapper;
import com.mercateo.rest.schemagen.util.EnumUtil;
import org.immutables.value.Value;

import com.google.common.annotations.VisibleForTesting;
import com.mercateo.immutables.DataClass;
import com.mercateo.rest.schemagen.constraints.SizeConstraints;
import com.mercateo.rest.schemagen.constraints.ValueConstraints;
import com.mercateo.rest.schemagen.generator.ObjectContext;

@Value.Immutable
@DataClass
public abstract class JsonProperty {

    static JsonProperty.Builder builderFor(ObjectContext<?> objectContext) {
        return JsonProperty.builder().withContext(objectContext);
    }

    static JsonProperty.Builder builderFor(Class<?> clazz) {
        return builder().withType(PropertyTypeMapper.of(clazz));
    }

    private static JsonProperty.Builder builder() {
        return new JsonProperty.Builder();
    }

    static JsonPropertyBuilder builderFrom(JsonProperty jsonProperty) {
        return builder().from(jsonProperty);
    }

    @Nullable
    public abstract String getId();

    @Nullable
    public abstract String getName();

    public abstract PropertyType getType();

    @Nullable
    public abstract String getRef();

    @Nullable
    public abstract Object getDefaultValue();

    @Value.Default
    public boolean isRequired() {
        return false;
    }

    public abstract List<JsonProperty> getProperties();

    @Value.Default
    public SizeConstraints getSizeConstraints() {
        return SizeConstraints.empty();
    }

    @Value.Default
    public ValueConstraints getValueConstraints() {
        return ValueConstraints.empty();
    }

    @Nullable
    public abstract Class<? extends IndividualSchemaGenerator> getIndividualSchemaGenerator();

    public abstract List<Object> getAllowedValues();

    @Nullable
    public abstract String getPath();

    @VisibleForTesting
    JsonProperty getPropertyByName(String name) {
        for (JsonProperty p : getProperties()) {
            if (p.getName().equals(name))
                return p;
        }
        throw new NoSuchElementException(String.format("[%s] does not have a property named  '%s'", this, name));
    }

    static class Builder extends JsonPropertyBuilder {
        public Builder withContext(ObjectContext<?> objectContext) {
            Class<?> rawType = objectContext.getRawType();
            withType(objectContext.getPropertyType());
            withDefaultValue(objectContext.getDefaultValue(), rawType);
            withAllowedValues(objectContext.getAllowedValues(), rawType);
            withIsRequired(objectContext.isRequired());
            withSizeConstraints(objectContext.getSizeConstraints());
            withValueConstraints(objectContext.getValueConstraints());
            withIndividualSchemaGenerator(objectContext.getSchemaGenerator());
            return this;
        }

        public Builder withDefaultValue(Object defaultValue, Class<?> rawType) {
            withDefaultValue(convertToString(defaultValue, rawType));
            return this;
        }

        public Builder withAllowedValues(List<?> allowedValues, Class<?> rawType) {
            if (allowedValues != null && !allowedValues.isEmpty()) {
                withAllowedValues(allowedValues
                    .stream()
                    .map(o -> convertToString(o, rawType))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet()));
            } else if (Enum.class.isAssignableFrom(rawType)) {
                withAllowedValues(Stream.of(rawType.getEnumConstants()).map(this::convertEnumToString).collect(
                        Collectors.toList()));
            }
            return this;
        }

        @SuppressWarnings("unchecked")
        private Object convertToString(Object defaultValue, Class<?> rawType) {
            if (defaultValue == null) {
                return null;
            }
            if (Enum.class.isAssignableFrom(rawType)) {
                return convertEnumToString(defaultValue);
            } else {
                return defaultValue;
            }
        }

        private String convertEnumToString(Object defaultValue) {
            return EnumUtil.convertToString((Enum<? extends Enum<?>>) defaultValue);
        }
    }
}
