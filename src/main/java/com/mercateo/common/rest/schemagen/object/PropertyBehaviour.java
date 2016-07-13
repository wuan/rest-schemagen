package com.mercateo.common.rest.schemagen.object;

import com.mercateo.common.rest.schemagen.internal.DataClassStyle;
import org.immutables.value.Value;

import java.util.List;
import java.util.function.Predicate;

@Value.Immutable
@DataClassStyle
public interface PropertyBehaviour {

    default Predicate<DataObject> propertyChecker() {
        return dataObject -> true;
    }

    List<RawDataPropertyCollector> propertyCollectors();

    static PropertyBehaviourBuilder builder() {
        return new PropertyBehaviourBuilder();
    }
}
