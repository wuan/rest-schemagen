package com.mercateo.common.rest.schemagen.better.property;

import com.mercateo.common.rest.schemagen.better.property.FieldCollectorConfigBuilder;
import com.mercateo.common.rest.schemagen.internal.DataClassStyle;
import org.immutables.value.Value;

@Value.Immutable
@DataClassStyle
public interface FieldCollectorConfig {

    @Value.Default
    default boolean includePrivateFields() {
        return true;
    }

    static FieldCollectorConfigBuilder builder() {
        return new FieldCollectorConfigBuilder();
    }
}
