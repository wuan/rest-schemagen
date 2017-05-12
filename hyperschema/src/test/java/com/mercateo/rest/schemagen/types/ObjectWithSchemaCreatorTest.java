package com.mercateo.rest.schemagen.types;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ObjectWithSchemaCreatorTest {
    @Test
    public void shouldWrapObjectAndSchema() throws Exception {
        Object object = new Object();
        JsonHyperSchema schema = mock(JsonHyperSchema.class);

        ObjectWithSchema<Object> objectWithSchema = new ObjectWithSchemaCreator().create(object, schema);

        Assertions.assertThat(objectWithSchema.object).isEqualTo(object);
        Assertions.assertThat(objectWithSchema.schema).isEqualTo(schema);
    }
}