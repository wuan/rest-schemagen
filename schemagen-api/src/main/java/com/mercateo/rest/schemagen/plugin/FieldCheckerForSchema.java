package com.mercateo.rest.schemagen.plugin;

import com.mercateo.rest.schemagen.PropertyContext;

import java.lang.reflect.Field;
import java.util.function.BiPredicate;

/**
 * this class checks, if a field of a bean should be contained in the schema
 * 
 * @author joerg_adler
 *
 */
public interface FieldCheckerForSchema extends BiPredicate<Field, PropertyContext> {
    static FieldCheckerForSchema fromBiPredicate(BiPredicate<Field, PropertyContext> predicate) {
        return predicate::test;
    }
}
