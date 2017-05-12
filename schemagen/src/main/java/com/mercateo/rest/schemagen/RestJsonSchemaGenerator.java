package com.mercateo.rest.schemagen;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.rest.schemagen.generator.JsonPropertyResult;
import com.mercateo.rest.schemagen.generator.ObjectContext;
import com.mercateo.rest.schemagen.generictype.GenericType;
import com.mercateo.rest.schemagen.json.mapper.PropertyJsonSchemaMapper;
import com.mercateo.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.rest.schemagen.property.PropertyType;

public class RestJsonSchemaGenerator implements JsonSchemaGenerator {
    private static final Set<Class<?>> INVALID_OUTPUT_TYPES = new HashSet<>(Arrays.asList(void.class, Void.class));

    private static final Logger logger = LoggerFactory.getLogger(RestJsonSchemaGenerator.class);

    private final SchemaPropertyGenerator schemaPropertyGenerator;

    private final PropertyJsonSchemaMapper propertyJsonSchemaMapper;

    private final boolean isDebugEnabled;

    public RestJsonSchemaGenerator() {
        schemaPropertyGenerator = new SchemaPropertyGenerator();
        propertyJsonSchemaMapper = new PropertyJsonSchemaMapper();
        isDebugEnabled = logger.isDebugEnabled();
    }

    @Override
    public <T> Optional<ObjectNode> createSchema(Class<T> clazz, Type type, PropertyContext<T> propertyContext,
            FieldCheckerForSchema fieldCheckerForSchema) {
        final GenericType<T> genericType = GenericType.of(type, clazz);

        final SchemaPropertyContext schemaPropertyContext = new SchemaPropertyContext(propertyContext,
                fieldCheckerForSchema);

        if (!INVALID_OUTPUT_TYPES.contains(genericType.getRawType())) {
            return generateJsonSchema(ObjectContext.buildFor(genericType).build(), schemaPropertyContext);
        } else {
            return Optional.empty();
        }
    }

    private Optional<ObjectNode> generateJsonSchema(ObjectContext<?> objectContext,
            SchemaPropertyContext schemaPropertyContext) {
        final JsonPropertyResult jsonPropertyResult = schemaPropertyGenerator.generateSchemaProperty(objectContext,
                schemaPropertyContext);
        JsonProperty rootJsonProperty = jsonPropertyResult.getRoot();
        if (rootJsonProperty.getProperties().isEmpty() && rootJsonProperty.getType() == PropertyType.OBJECT) {
            return Optional.empty();
        }
        return Optional.of(propertyJsonSchemaMapper.toJson(jsonPropertyResult));
    }

    private ObjectNode createObjectNode() {
        return new ObjectNode(new JsonNodeFactory(true));
    }
}
