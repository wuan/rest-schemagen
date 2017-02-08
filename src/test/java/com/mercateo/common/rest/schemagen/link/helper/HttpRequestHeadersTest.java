package com.mercateo.common.rest.schemagen.link.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

public class HttpRequestHeadersTest {

    @Test
    public void shouldIgnoreCaseOfName() throws Exception {
        final HashMap<String, List<String>> requestHeaders = new HashMap<>();
        requestHeaders.put("foo", Collections.singletonList("bar"));

        final HttpRequestHeaders headers = new HttpRequestHeaders(requestHeaders);

        assertThat(headers.getHeaderValues("Foo")).containsExactly("bar");
    }

    @Test
    public void shouldCreateRequestHeaders() throws Exception {
        final HashMap<String, List<String>> requestHeaders = new HashMap<>();
        requestHeaders.put("foo", Arrays.asList("baz", "qux"));
        requestHeaders.put("bar", Collections.singletonList("quux"));

        final HttpRequestHeaders headers = new HttpRequestHeaders(requestHeaders);

        assertThat(headers.getHeaderValues("foo")).containsExactly("baz", "qux");
        assertThat(headers.getHeaderValues("bar")).containsExactly("quux");
    }

    @Test
    public void shouldIgnoreUppercase() throws Exception {
        final HashMap<String, List<String>> requestHeaders = new HashMap<>();
        requestHeaders.put("foo", Arrays.asList("baz", "qux"));
        requestHeaders.put("Foo", Collections.singletonList("quux"));

        final HttpRequestHeaders headers = new HttpRequestHeaders(requestHeaders);

        assertThat(headers.getHeaderValues("foo")).containsExactlyInAnyOrder("baz", "qux", "quux");
    }

    @Test
    public void shouldCreateCopy() throws Exception {
        final HashMap<String, List<String>> requestHeaders = new HashMap<>();
        requestHeaders.put("foo", Collections.singletonList("bar"));

        final HttpRequestHeaders headers = new HttpRequestHeaders(requestHeaders);

        final HttpRequestHeaders headersCopy = new HttpRequestHeaders(headers);

        assertThat(headersCopy.getHeaderValues("foo")).containsExactly("bar");
    }
}