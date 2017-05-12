package com.mercateo.rest.schemagen.property;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum PropertyType {
    OBJECT, STRING, BOOLEAN, INTEGER, NUMBER, ARRAY;

    public static final Set<PropertyType> PRIMITIVE_TYPES = new HashSet<>(Arrays.asList(STRING,
            BOOLEAN, INTEGER, NUMBER));
}
