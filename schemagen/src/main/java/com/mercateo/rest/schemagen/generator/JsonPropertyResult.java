package com.mercateo.rest.schemagen.generator;

import com.mercateo.immutables.Tuple;
import com.mercateo.rest.schemagen.JsonProperty;
import org.immutables.value.Value;

import java.util.Set;

@Value.Immutable
@Tuple
public interface JsonPropertyResult {
    JsonProperty getRoot();
    Set<JsonProperty> getReferencedElements();
}
