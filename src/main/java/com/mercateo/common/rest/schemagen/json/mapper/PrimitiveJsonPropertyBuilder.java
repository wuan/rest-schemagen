package com.mercateo.common.rest.schemagen.json.mapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.common.rest.schemagen.JsonProperty;

import java.util.function.Function;

class PrimitiveJsonPropertyBuilder {
    final private GenericJsonPropertyMapper genericJsonPropertyMapper;

    final private JsonNodeFactory nodeFactory;
    private ObjectNode propertyNode;
    private JsonProperty jsonProperty;

    PrimitiveJsonPropertyBuilder(JsonNodeFactory nodeFactory) {
        this.genericJsonPropertyMapper = new GenericJsonPropertyMapper(nodeFactory);
        this.nodeFactory = nodeFactory;
    }

    PrimitiveJsonPropertyBuilder createObjectNode(JsonProperty jsonProperty) {
        this.jsonProperty = jsonProperty;
        propertyNode = new ObjectNode(nodeFactory);
        return this;
    }

    PrimitiveJsonPropertyBuilder withType(String type) {
        propertyNode.put("type", type);
        return this;
    }

    PrimitiveJsonPropertyBuilder withDefaultAndAllowedValues(Function<Object, JsonNode> nodeCreator) {
        genericJsonPropertyMapper.addDefaultAndAllowedValues(propertyNode, this.jsonProperty, nodeCreator);
        return this;
    }

    PrimitiveJsonPropertyBuilder withDefaultValue(JsonNode value) {
        propertyNode.set("default", value);
        return this;
    }

    ObjectNode build() {
        return propertyNode;
    }

}
