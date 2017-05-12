package com.mercateo.rest.schemagen;

import java.util.List;
import java.util.Set;

import com.mercateo.immutables.DataClass;
import org.immutables.value.Value;

@Value.Immutable
@DataClass
public interface PropertyContext<T> {

    List<T> getAllowedValues();

    default boolean hasAllowedValues() {
        return !getAllowedValues().isEmpty();
    }

    T getDefaultValue();

    default boolean hasDefaultValue() {
        return getDefaultValue() != null;
    }

    Set<Class> getViewClasses();

    static PropertyContextBuilder builder() {
        return new PropertyContextBuilder();
    }
}
