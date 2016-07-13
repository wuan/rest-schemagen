package com.mercateo.common.rest.schemagen.plugin;

import com.mercateo.common.rest.schemagen.object.DataProperty;

import java.util.function.Predicate;

public interface PropertyEnabledChecker extends Predicate<DataProperty> {
}
