package com.mercateo.rest.schemagen;

import com.mercateo.rest.schemagen.plugin.FieldCheckerForSchema;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Field;


public class SchemaPropertyContext {

    private final PropertyContext callContext;

    private final FieldCheckerForSchema fieldCheckerForSchema;

    public SchemaPropertyContext(PropertyContext callContext,
            FieldCheckerForSchema fieldCheckerForSchema) {
        this.callContext = checkNotNull(callContext);
        this.fieldCheckerForSchema = checkNotNull(fieldCheckerForSchema);
    }

    public boolean isFieldApplicable(Field field) {
        checkNotNull(field);
        return fieldCheckerForSchema.test(field, callContext);
    }
}
