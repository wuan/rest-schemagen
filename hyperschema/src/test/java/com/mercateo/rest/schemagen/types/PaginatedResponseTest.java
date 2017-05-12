package com.mercateo.rest.schemagen.types;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.mercateo.common.rest.schemagen.JsonHyperSchema;
import com.mercateo.rest.schemagen.link.relation.RelationContainer;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import javax.ws.rs.core.Link;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PaginatedResponseTest {

    @Test
    public void testPaginatedResponseBuilder() {

        final Link containerLink = Link.fromPath("/").build();
        final PaginatedResponse<String> listResponse = PaginatedResponse.<Integer, String>builder()
                .withList(Arrays.asList(1, 2, 3), 1, 2)
                .withElementMapper(this::elementMapper)
                .withPaginationLinkCreator(this::paginationLinkCreator)
                .withContainerLinks(containerLink)
                .build();

        final List<String> strings = listResponse.object.members.stream()
                .map(o -> o.object)
                .collect(Collectors.toList());
        assertThat(strings).containsExactly("2", "3");

        final List<String> links = listResponse.object.members.stream().map(
                o -> o.schema.getLinks().iterator().next().getUri().toString()).collect(
                Collectors.toList());
        assertThat(links).containsExactly("/2", "/3");

        Assertions.assertThat(listResponse.schema.getLinks().iterator().next().getUri().toString()).isEqualTo(
                "/");
    }

    @Test
    public void testPaginagedResponseBuilderWithList() {
        final PaginatedList<Integer> paginatedList = new PaginatedList<>(10, 3, 2, Arrays.asList(1, 3));
        final Link containerLink = Link.fromPath("/").build();
        final PaginatedResponse<String> listResponse = PaginatedResponse.<Integer, String>builder()
                .withList(paginatedList)
                .withElementMapper(this::elementMapper)
                .withPaginationLinkCreator(this::paginationLinkCreator)
                .withContainerLinks(containerLink)
                .build();

        Assertions.assertThat(listResponse.object.total).isEqualTo(paginatedList.total);
        Assertions.assertThat(listResponse.object.offset).isEqualTo(paginatedList.offset);
        Assertions.assertThat(listResponse.object.limit).isEqualTo(paginatedList.limit);
    }

    private ObjectWithSchema<String> elementMapper(Integer number) {
        final Link link = Link.fromPath("/" + number).build();
        return ObjectWithSchema.create(Integer.toHexString(number), JsonHyperSchema.from(link));
    }

    private Optional<Link> paginationLinkCreator(RelationContainer rel, int offset, int limit) {
        return Optional.of(Link.fromPath("/").param("offset", Integer.toString(offset)).param("limit", Integer.toString(limit)).build());
    }

    static class Payload {
        public String value;
    }

    @Test
    public void shouldSerialize() throws Exception {
        final ObjectMapper objectMapper = new ObjectMapper();

        final Payload payload = new Payload();
        payload.value = "foo";
        final ObjectWithSchema<Payload> element = ObjectWithSchema.create(payload, JsonHyperSchema.from(Collections
                .emptyList()));
        final PaginatedResponse<Payload> listResponse = PaginatedResponse.create(Collections.singletonList(element), 100, 10, 5,
                JsonHyperSchema.from(Collections.emptyList()));

        final String jsonString = objectMapper.writeValueAsString(listResponse);

        assertThat(jsonString).isEqualTo(
                "{\"members\":[{\"value\":\"foo\",\"_schema\":{\"links\":[]}}],\"total\":100,\"offset\":10,\"limit\":5,\"_schema\":{\"links\":[]}}");
    }

    @Test
    public void shouldDeserialize() throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        final TypeFactory typeFactory = mapper.getTypeFactory();

        final String content = "{\"members\": [{\"value\": \"foo\", \"_schema\":{\"links\": []}}, {\"value\": \"bar\", \"_schema\":{\"links\": []}}], \"offset\": 100, \"limit\": 10, \"total\": 2000, \"_schema\":{\"links\":[]}}";

        final JavaType nameWithSchemaType = typeFactory.constructParametricType(PaginatedResponse.class, Payload.class);
        final PaginatedResponse<Payload> listResponse = mapper.readValue(content, nameWithSchemaType);

        Assertions.assertThat(listResponse.getMembers()).extracting(ObjectWithSchema::getObject).extracting(p -> p.value).containsExactly("foo", "bar");
        Assertions.assertThat(listResponse.getLimit()).isEqualTo(10);
        Assertions.assertThat(listResponse.getOffset()).isEqualTo(100);
        Assertions.assertThat(listResponse.getTotal()).isEqualTo(2000);
    }

}