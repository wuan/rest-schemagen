package com.mercateo.rest.schemagen.link;


import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LinkFactoryContextDefaultTest {
    @Test
    public void shouldBeInstantiableWithDefaultConstructor() throws Exception {
        final LinkFactoryContextDefault linkFactoryContext = new LinkFactoryContextDefault();

        Assertions.assertThat(linkFactoryContext).isNotNull();
        Assertions.assertThat(linkFactoryContext.getBaseUri()).isNull();
        Assertions.assertThat(linkFactoryContext.getFieldCheckerForSchema()).isNull();
        Assertions.assertThat(linkFactoryContext.getMethodCheckerForLink()).isNull();
    }
}