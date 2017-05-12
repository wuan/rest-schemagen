package com.mercateo.rest.schemagen;

import java.util.Collection;

import javax.ws.rs.core.Link;

public class JsonHyperSchemaCreator {

    public JsonHyperSchema from(Collection<Link> links) {
        return JsonHyperSchema.from(links);
    }

}
