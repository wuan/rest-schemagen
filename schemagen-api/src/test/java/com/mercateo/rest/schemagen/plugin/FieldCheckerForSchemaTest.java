package com.mercateo.rest.schemagen.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.mercateo.rest.schemagen.PropertyContext;

public class FieldCheckerForSchemaTest {

    @Test
    public void shouldBeCreateableFromBiPredicate() throws Exception {
        final PropertyContext enabledCallContext = mock(PropertyContext.class);
        final FieldCheckerForSchema fieldCheckerForSchema = FieldCheckerForSchema.fromBiPredicate((
                field, callContext) -> field == null && callContext == enabledCallContext);

        assertThat(fieldCheckerForSchema.test(null, enabledCallContext)).isTrue();
        assertThat(fieldCheckerForSchema.test(null, null)).isFalse();
    }
}