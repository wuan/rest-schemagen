package com.mercateo.common.rest.schemagen.object;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class SchemaConfigurationTest {

    @Test
    public void fieldCheckerShouldReturnTrueForSinglePositiveElement() {
        final SchemaConfiguration schemaConfiguration = SchemaConfiguration.builder().addFieldCheckers(field -> true).build();

        assertThat(schemaConfiguration.fieldChecker().test(null)).isTrue();
    }

    @Test
    public void fieldCheckerShouldReturnFalseForSingleNegativeElement() {
        final SchemaConfiguration schemaConfiguration = SchemaConfiguration.builder().addFieldCheckers(field -> false).build();

        assertThat(schemaConfiguration.fieldChecker().test(null)).isFalse();
    }

    @Test
    public void fieldCheckerShouldReturnFalseForNegativeElementAtSecondPosition() {
        final SchemaConfiguration schemaConfiguration = SchemaConfiguration.builder()
                .addFieldCheckers(field -> true)
                .addFieldCheckers(field -> false)
                .build();

        assertThat(schemaConfiguration.fieldChecker().test(null)).isFalse();
    }
    
    @Test
    public void methodCheckerShouldReturnTrueForSinglePositiveElement() {
        final SchemaConfiguration schemaConfiguration = SchemaConfiguration.builder().addMethodCheckers(method -> true).build();

        assertThat(schemaConfiguration.methodChecker().test(null)).isTrue();
    }

    @Test
    public void methodCheckerShouldReturnFalseForSingleNegativeElement() {
        final SchemaConfiguration schemaConfiguration = SchemaConfiguration.builder().addMethodCheckers(method -> false).build();

        assertThat(schemaConfiguration.methodChecker().test(null)).isFalse();
    }

    @Test
    public void methodCheckerShouldReturnFalseForNegativeElementAtSecondPosition() {
        final SchemaConfiguration schemaConfiguration = SchemaConfiguration.builder()
                .addMethodCheckers(method -> true)
                .addMethodCheckers(method -> false)
                .build();

        assertThat(schemaConfiguration.methodChecker().test(null)).isFalse();
    }
}