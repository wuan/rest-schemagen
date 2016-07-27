package com.mercateo.common.rest.schemagen.better.process;

import java.util.Arrays;

import com.mercateo.common.rest.schemagen.better.property.Property;
import com.mercateo.common.rest.schemagen.generictype.GenericType;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.mercateo.common.rest.schemagen.better.property.FieldCollector;
import com.mercateo.common.rest.schemagen.better.property.FieldCollectorConfig;
import com.mercateo.common.rest.schemagen.better.property.PropertyBuilder;

import static com.mercateo.common.rest.schemagen.better.property.PropertyBuilderTest.getFirstElement;
import static org.assertj.core.api.Assertions.assertThat;

public class UnwrappedPropertyBuilderTest {

    private UnwrappedPropertyBuilder unwrappedPropertyBuilder;

    @Before
    public void setUp() throws Exception {
        PropertyBuilder propertyBuilder = new PropertyBuilder(Arrays.asList(new FieldCollector(FieldCollectorConfig.builder()
                .build())));
        unwrappedPropertyBuilder = new UnwrappedPropertyBuilder(propertyBuilder);
    }

    @Test
    public void singleLevelUnwrap() throws Exception {
        final Property unwrappedProperty = unwrappedPropertyBuilder.from(PropertyHolder.class);

        assertThat(unwrappedProperty.children()).extracting(Property::name).containsExactlyInAnyOrder("foo", "bar");
    }

    @Test
    public void twoLevelUnwrap() throws Exception {
        final Property unwrappedProperty = unwrappedPropertyBuilder.from(SecondLevelPropertyHolder.class);

        assertThat(unwrappedProperty.children()).extracting(Property::name).containsExactlyInAnyOrder("foo", "bar");
    }

    @Test
    public void singleLedvelUnwrap() throws Exception {
        final Property unwrappedProperty = unwrappedPropertyBuilder.from(PropertyHolder.class);

        final PropertyHolder propertyHolder = new PropertyHolder();
        propertyHolder.unwrappedPropertyHolder = new UnwrappedPropertyHolder();
        propertyHolder.unwrappedPropertyHolder.foo = "value1";

        final Property firstElement = getFirstElement(unwrappedProperty.children());

        assertThat(firstElement.getValue(propertyHolder)).isEqualTo("value1");
    }

    static class SecondLevelPropertyHolder {
        @JsonUnwrapped
        PropertyHolder propertyHolder;

    }

    static class PropertyHolder {
        @JsonUnwrapped
        UnwrappedPropertyHolder unwrappedPropertyHolder;

    }

    static class UnwrappedPropertyHolder {
        String foo;

        String bar;
    }
}