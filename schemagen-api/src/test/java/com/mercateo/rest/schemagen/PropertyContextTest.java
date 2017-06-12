package com.mercateo.rest.schemagen;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class PropertyContextTest {

    static class PropertyA {
        Integer count;

        PropertyB inner;
    }

    static class PropertyB {
        String value;
    }

    @Test
    public void shouldCreateInnerWithDefaultValue() throws Exception {
        final PropertyA propertyA = createProperty("foo", 5);

        PropertyContextBuilder<PropertyA> builder = PropertyContext.builder();
        builder.withDefaultValue(propertyA);
        final PropertyContext<PropertyA> propertyAContext = builder.build();

        final PropertyContext<PropertyB> propertyBContext = propertyAContext.createInner(
                propA -> propA.inner);

        assertThat(propertyBContext.getDefaultValue()).contains(propertyA.inner);
    }

    @Test
    public void shouldCreateInnerWithoutDefaultValue() throws Exception {
        PropertyContextBuilder<PropertyA> builder = PropertyContext.builder();
        final PropertyContext<PropertyA> propertyAContext = builder.build();

        final PropertyContext<PropertyB> propertyBContext = propertyAContext.createInner(
                propA -> propA.inner);

        assertThat(propertyBContext.getDefaultValue()).isEmpty();
    }

    @Test
    public void shouldCreateInnerWithNullResultForDefaultValue() throws Exception {
        final PropertyA propertyA = new PropertyA();

        PropertyContextBuilder<PropertyA> builder = PropertyContext.builder();
        builder.withDefaultValue(propertyA);
        final PropertyContext<PropertyA> propertyAContext = builder.build();

        final PropertyContext<PropertyB> propertyBContext = propertyAContext.createInner(
                propA -> propA.inner);

        assertThat(propertyBContext.getDefaultValue()).isEmpty();
    }

    @Test
    public void shouldCreateInnerWithAllowedValues() throws Exception {
        final PropertyA propertyA1 = createProperty("bar", 6);
        final PropertyA propertyA2 = createProperty("baz", 7);

        PropertyContextBuilder<PropertyA> builder = PropertyContext.builder();
        builder.addAllowedValues(propertyA1, propertyA2);
        final PropertyContext<PropertyA> propertyAContext = builder.build();

        final PropertyContext<PropertyB> propertyBContext = propertyAContext.createInner(
                propA -> propA.inner);

        assertThat(propertyBContext.getAllowedValues()).containsExactlyInAnyOrder(propertyA1.inner,
                propertyA2.inner);
    }

    private PropertyA createProperty(String value, int count) {
        final PropertyB propertyB = new PropertyB();
        propertyB.value = value;

        final PropertyA propertyA = new PropertyA();
        propertyA.count = count;
        propertyA.inner = propertyB;
        return propertyA;
    }
}