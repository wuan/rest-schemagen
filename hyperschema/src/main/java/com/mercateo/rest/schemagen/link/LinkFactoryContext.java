package com.mercateo.rest.schemagen.link;

import com.mercateo.rest.schemagen.plugin.FieldCheckerForSchema;
import com.mercateo.rest.schemagen.plugin.MethodCheckerForLink;

import java.net.URI;

public interface LinkFactoryContext {
    URI getBaseUri();

    FieldCheckerForSchema getFieldCheckerForSchema();

    MethodCheckerForLink getMethodCheckerForLink();
}
