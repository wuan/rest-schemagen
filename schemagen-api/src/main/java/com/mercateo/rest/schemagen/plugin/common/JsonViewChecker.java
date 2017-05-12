package com.mercateo.rest.schemagen.plugin.common;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonView;
import com.mercateo.rest.schemagen.PropertyContext;
import com.mercateo.rest.schemagen.plugin.FieldCheckerForSchema;

public class JsonViewChecker implements FieldCheckerForSchema {

    @Override
    public boolean test(Field field, PropertyContext context) {
        checkNotNull(field);
        checkNotNull(context);
        final JsonView jsonView = field.getAnnotation(JsonView.class);
        if (jsonView != null) {
            @SuppressWarnings("unchecked")
            Set<Class> viewClasses = context.getViewClasses();
            return viewClasses.isEmpty() || Arrays.stream(jsonView.value()).anyMatch(
                    viewClasses::contains);
        }
        return true;
    }

}
