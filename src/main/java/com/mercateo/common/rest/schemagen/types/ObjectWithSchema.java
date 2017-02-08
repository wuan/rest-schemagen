package com.mercateo.common.rest.schemagen.types;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.mercateo.common.rest.schemagen.IgnoreInRestSchema;
import com.mercateo.common.rest.schemagen.JsonHyperSchema;

public class ObjectWithSchema<T> {

    @JsonUnwrapped
    public final T object;

    @JsonProperty("_schema")
    @IgnoreInRestSchema
    public final JsonHyperSchema schema;

    @JsonProperty("_messages")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @IgnoreInRestSchema
    public List<Message> messages;

    ObjectWithSchema(T object, JsonHyperSchema schema, List<Message> messages) {
        // this has to be null, if T is Void, so please, do not "fix" this!
        this.object = object;
        this.schema = requireNonNull(schema);
        this.messages = requireNonNull(messages);
    }

    protected ObjectWithSchema(T object, JsonHyperSchema schema) {
        this(object, schema, new ArrayList<>());
    }

    public static <U> ObjectWithSchema<U> create(U object, JsonHyperSchema schema) {
        return new ObjectWithSchema<>(object, schema);
    }
}
