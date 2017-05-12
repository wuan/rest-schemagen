package com.mercateo.rest.schemagen.types;

public class ListResponseBuilderCreator {
    public <ElementIn, ElementOut> ListResponseBuilder<ElementIn, ElementOut> builder() {
        return ListResponse.<ElementIn, ElementOut> builder();
    }
}
