package com.mercateo.rest.schemagen.link.relation;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class RelationDefault0Test {

    private RelationTypeDefault type;

    private RelationDefault relation;

    private String relationName;

    @Before
    public void setUp() {
        type = new RelationTypeDefault("<typeName>", false, "<type>");
        relationName = "<name>";
        relation = new RelationDefault(relationName, type);
    }

    @Test
    public void testGetName() {
        Assertions.assertThat(relation.getName()).isEqualTo(relationName);
    }

    @Test
    public void testGetType() {
        Assertions.assertThat(relation.getType()).isEqualTo(type);
    }
}