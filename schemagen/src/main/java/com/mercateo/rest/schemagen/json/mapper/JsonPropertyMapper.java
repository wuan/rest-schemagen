package com.mercateo.rest.schemagen.json.mapper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mercateo.rest.schemagen.JsonProperty;

interface JsonPropertyMapper {
    ObjectNode toJson(JsonProperty jsonProperty);
}
