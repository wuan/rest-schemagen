package com.mercateo.common.rest.schemagen.object;

import com.mercateo.common.rest.schemagen.internal.TupleStyle;
import org.immutables.value.Value;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.function.Function;

@Value.Immutable
@TupleStyle
public interface DataProperty <U> {
    String name();

    Map<Class<? extends Annotation>, ? extends Annotation> annotations();

    DataObject<U> object();

    Function<?, U> valueAccessor();
}
