package com.mercateo.rest.schemagen;

import java.util.Optional;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.rest.schemagen.generictype.GenericType;
import com.mercateo.rest.schemagen.plugin.FieldCheckerForSchema;

public interface JsonSchemaGenerator {

    /**
     *
     * @param genericType
     *            type for which the schema should be generated
     * @param fieldCheckerForSchema
     *            callback to check if a certain field should be included
     * @param propertyContext
     *            context information like default and allowed values
     *
     * @return schema representation as JSON object
     */
    <T> Optional<ObjectNode> createSchema(GenericType<T> genericType,
            PropertyContext<T> propertyContext, FieldCheckerForSchema fieldCheckerForSchema);
}