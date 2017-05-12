package com.mercateo.rest.schemagen;

import java.lang.reflect.Type;
import java.util.Optional;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.rest.schemagen.plugin.FieldCheckerForSchema;

public interface JsonSchemaGenerator {

    /**
     *
     * @param clazz
     *            type for which the schema should be generated
     * @param type
     *            type for which the schema should be generated
     * @param fieldCheckerForSchema
     *            callback to check if a certain field should be included
     * @param propertyContext
     *            context information like default and allowed values
     *
     * @return schema representation as JSON object
     */
    <T> Optional<ObjectNode> createSchema(Class<T> clazz, Type type, PropertyContext<T> propertyContext,
            FieldCheckerForSchema fieldCheckerForSchema);
}