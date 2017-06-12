package com.mercateo.rest.schemagen;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.immutables.value.Value;

import com.mercateo.immutables.DataClass;

@Value.Immutable
@DataClass
public abstract class PropertyContext<T> {

    public abstract Set<T> getAllowedValues();

    public abstract Optional<T> getDefaultValue();

    public abstract Set<Class> getViewClasses();

    static <U> PropertyContextBuilder<U> builder() {
        return new PropertyContextBuilder<>();
    }

    public <U> PropertyContext<U> createInner(Function<T, U> valueAccessor) {
        final PropertyContextBuilder<U> builder = PropertyContext.builder();

        builder.withAllowedValues(getAllowedValues().stream().map(valueAccessor).filter(
                Objects::nonNull).collect(Collectors.toSet()));

        getDefaultValue().ifPresent(defaultValue -> {
            final U innerDefaultValue = valueAccessor.apply(defaultValue);
            if (innerDefaultValue != null) {
                builder.withDefaultValue(innerDefaultValue);
            }
        });

        builder.withViewClasses(getViewClasses());

        return builder.build();
    }
}
