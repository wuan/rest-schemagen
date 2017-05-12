package com.mercateo.rest.schemagen.link.relation;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class RelationTypeDefault0Test {

    public static final String NAME = "<name>";
    public static final String SERIALIZED_NAME = "<NAME>";
    public static final boolean SHOULD_BE_SERIALIZED = true;
    private RelationTypeDefault relationType;

    @Before
    public void setUp() {
        relationType = new RelationTypeDefault(NAME, SHOULD_BE_SERIALIZED, SERIALIZED_NAME);
    }

    @Test
    public void testGetName() {
        Assertions.assertThat(relationType.getName()).isEqualTo(NAME);
    }

    @Test
    public void testIsShouldBeSerialized() {
        Assertions.assertThat(relationType.isShouldBeSerialized()).isTrue();
    }

    @Test
    public void testGetSerializedName() {
        Assertions.assertThat(relationType.getSerializedName()).isEqualTo(SERIALIZED_NAME);
    }

}