package com.mercateo.rest.schemagen;

import java.util.List;

import com.mercateo.rest.schemagen.types.PaginatedList;

public class TestPaginatedResponse extends PaginatedList<TestRto> {
    public TestPaginatedResponse(int total, int offset, int limit, List<TestRto> members) {
        super(total, offset, limit, members);
    }
}
