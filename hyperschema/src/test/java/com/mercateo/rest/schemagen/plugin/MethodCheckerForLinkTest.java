package com.mercateo.rest.schemagen.plugin;


import com.mercateo.rest.schemagen.link.Scope;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class MethodCheckerForLinkTest {
    @Test
    public void shouldBeCreateableFromPredicate() throws Exception {
        final Scope scope = mock(Scope.class);
        final MethodCheckerForLink methodCheckerForLink = MethodCheckerForLink.fromPredicate(x -> x == scope);

        assertThat(methodCheckerForLink.test(scope)).isTrue();
        assertThat(methodCheckerForLink.test(null)).isFalse();
    }
}