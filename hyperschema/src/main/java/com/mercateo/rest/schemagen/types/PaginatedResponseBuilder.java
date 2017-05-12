package com.mercateo.rest.schemagen.types;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mercateo.common.rest.schemagen.JsonHyperSchema;
import com.mercateo.common.rest.schemagen.ListSlicer;
import com.mercateo.common.rest.schemagen.PaginationLinkBuilder;
import com.mercateo.common.rest.schemagen.PaginationLinkBuilder.PaginationLinkCreator;

import javax.ws.rs.core.Link;

public class PaginatedResponseBuilder<ElementIn, ElementOut> extends
        ResponseBuilderAbstract<PaginatedResponseBuilder<ElementIn, ElementOut>, ElementIn, ElementOut, PaginatedResponse<ElementOut>> {

    private PaginationLinkCreator paginationLinkCreator;

    private PaginatedList<ElementIn> paginatedList;

    /**
     * @deprecated please use {@link PaginatedResponse#builder()} instead
     */
    @Deprecated
    public PaginatedResponseBuilder() {
    }

    @Override
    public PaginatedResponse<ElementOut> build() {
        requireNonNull(paginatedList);
        requireNonNull(elementMapper);
        requireNonNull(containerLinks);
        requireNonNull(paginationLinkCreator);

        PaginatedList<ObjectWithSchema<ElementOut>> mappedList = new PaginatedList<>(
                paginatedList.total, paginatedList.offset, paginatedList.limit,
                paginatedList.members.stream().map(elementMapper).collect(Collectors.toList()));

        final List<Link> containerLinks = new ArrayList<>(this.containerLinks);

        containerLinks.addAll(PaginationLinkBuilder.of(paginatedList.total, paginatedList.offset,
                paginatedList.limit).generateLinks(paginationLinkCreator));

        JsonHyperSchema schema = JsonHyperSchema.from(containerLinks);
        return PaginatedResponse.create(mappedList.members, mappedList.total, mappedList.offset,
                mappedList.limit, schema);
    }

    public PaginatedResponseBuilder<ElementIn, ElementOut> withPaginationLinkCreator(
            PaginationLinkCreator paginationLinkCreator) {
        this.paginationLinkCreator = requireNonNull(paginationLinkCreator);
        return this;
    }

    public PaginatedResponseBuilder<ElementIn, ElementOut> withList(List<ElementIn> list,
            Integer offset, Integer limit) {
        paginatedList = ListSlicer.withDefaultInterval().create(offset, limit).createSliceOf(list);
        return this;
    }

    public PaginatedResponseBuilder<ElementIn, ElementOut> withList(
            PaginatedList<ElementIn> paginatedList) {
        this.paginatedList = paginatedList;
        return this;
    }
}
