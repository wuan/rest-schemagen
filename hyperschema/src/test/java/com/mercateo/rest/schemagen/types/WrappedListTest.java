package com.mercateo.rest.schemagen.types;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class WrappedListTest {

    static class Payload {
        public String value;
    }

    @Test
    public void shouldSerialize() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();

        final Payload payload = new Payload();
        payload.value = "foo";

        final WrappedList<Payload> wrappedList = new WrappedList<>(Collections.singletonList(payload));

        final String jsonString = objectMapper.writeValueAsString(wrappedList);

        assertThat(jsonString).isEqualTo("{\"members\":[{\"value\":\"foo\"}]}");
    }

    @Test
    public void shouldDeserialize() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final TypeFactory typeFactory = mapper.getTypeFactory();

        final String content = "{\"members\": [{\"value\": \"foo\", \"_schema\":{\"links\":[]}}, {\"value\": \"bar\", \"_schema\":{\"links\":[]}}]}";

        final TypeReference typeReference = new TypeReference<WrappedList<ObjectWithSchema<ListResponseTest.Payload>>>() {
        };
        final WrappedList<ObjectWithSchema<ListResponseTest.Payload>> listResponse = mapper.readValue(content, typeReference);

        Assertions.assertThat(listResponse.members)
                .extracting(ObjectWithSchema::getObject)
                .extracting(p -> p.value)
                .containsExactly("foo", "bar");
    }

}