package com.mercateo.common.rest.schemagen.better;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.ObjectArrayAssert;
import org.junit.Before;
import org.junit.Test;

public class PropertyBuilderTest {

    private PropertyBuilder propertyBuilder;

    @Before
    public void setUp() throws Exception {
        propertyBuilder = new PropertyBuilder();
    }

    @Test
    public void rootPropertyDefaultNameIsHashCharacter() {
        Property property = propertyBuilder.from(PropertyHolder.class);

        assertThat(property.name()).isEqualTo("#");
    }

    @Test
    public void callingValueAccessorOfRootElementThrows() {
        Property property = propertyBuilder.from(PropertyHolder.class);

        assertThatThrownBy(() -> property.getValue(null)).isInstanceOf(IllegalStateException.class)
                .hasMessage("cannot call value accessor for root element");
    }

    @Test
    public void containsChildFromField() throws Exception {
        Property property = propertyBuilder.from(PropertyHolder.class);

        assertThat(property.children()).extracting(Property::name).containsExactly("property");
    }

    @Test
    public void buildsPropertiesRecursively() throws Exception {
        Property property = propertyBuilder.from(TwoLevelPropertyHolder.class);

        final Collection<Property> children = property.children();
        final Property firstLevelElement = getFirstElement(children);
        final Property secondLevelElement = getFirstElement(firstLevelElement.children());

        assertThat(secondLevelElement.name()).isEqualTo("property");
    }

    @Test
    public void createsPropertyWithGenerics() throws Exception {

        Property property = propertyBuilder.from(TwoLevelPropertyHolder.class);

        final Property firstElement = getFirstElement(property.children());
        final Property secondLevelElement = getFirstElement(firstElement.children());
        assertThat(secondLevelElement.genericType().getRawType()).isEqualTo(String.class);
    }

    @Test
    public void propertyReturnsPropertyValue() throws Exception {
        Property property = propertyBuilder.from(PropertyHolder.class);

        final Property firstElement = getFirstElement(property.children());

        final PropertyHolder propertyHolder = new PropertyHolder();
        propertyHolder.property = "foo";

        assertThat(firstElement.getValue(propertyHolder)).isEqualTo("foo");
    }

    @Test
    public void propertyReturnsClassAnnotation() throws Exception {
        Property property = propertyBuilder.from(PropertyHolder.class);

        final Annotation firstElement = getFirstElement(property.annotations());
        assertThat(firstElement).isInstanceOf(Annotation1.class);
    }

    @Test
    public void subPropertyReturnsFieldAnnotation() throws Exception {
        Property property = propertyBuilder.from(TwoLevelPropertyHolder.class);

        final Property firstElement1 = getFirstElement(property.children());
        final Collection<Class<? extends Annotation>> annotations = firstElement1.annotations().stream().map(Annotation::annotationType).collect(Collectors.toList());
        assertThat(annotations).containsExactly(Annotation1.class, Annotation2.class);
    }

    @Test
    public void returnInheritedProperty() throws Exception {
        Property property = propertyBuilder.from(InheritedPropertyHolder.class);

        assertThat(property.children()).extracting(Property::name).containsExactly("property");
    }

    @Test
    public void nonObjectTypesShouldHaveNoChildren() throws Exception {
        Property property = propertyBuilder.from(PropertyHolder.class);

        final Property firstElement = getFirstElement(property.children());
        assertThat(firstElement.children()).isEmpty();
    }

    private <T> T getFirstElement(Collection<T> collection) {
        return collection.iterator().next();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Annotation1 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Annotation2 {
    }

    @Annotation1
    static class PropertyHolder {
        String property;
    }

    static class TwoLevelPropertyHolder {
        @Annotation1
        GenericPropertyHolder<String> holder;
    }

    @Annotation2
    static class GenericPropertyHolder<T> {
        T property;
    }

    static class InheritedPropertyHolder extends PropertyHolder {
    }
}