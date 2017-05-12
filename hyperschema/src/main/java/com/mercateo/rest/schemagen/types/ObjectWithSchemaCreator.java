package com.mercateo.rest.schemagen.types;

import com.mercateo.rest.schemagen.JsonHyperSchema;

public class ObjectWithSchemaCreator {

    public <T> ObjectWithSchema<T> create(T rto, JsonHyperSchema from) {
        return ObjectWithSchema.create(rto, from);
    }

}
