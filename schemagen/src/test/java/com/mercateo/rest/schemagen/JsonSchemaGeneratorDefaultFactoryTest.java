package com.mercateo.rest.schemagen;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonSchemaGeneratorDefaultFactoryTest {

    @Test
    public void provideMethodShouldReturnSchemaGenerator() {
        assertThat(new RestJsonSchemaGeneratorFactory().provide()).isNotNull();
    }

}