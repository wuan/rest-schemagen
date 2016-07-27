package com.mercateo.common.rest.schemagen.better.property;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.mercateo.common.rest.schemagen.better.property.FieldCollector;
import com.mercateo.common.rest.schemagen.better.property.FieldCollectorConfig;
import com.mercateo.common.rest.schemagen.better.property.MethodCollector;
import com.mercateo.common.rest.schemagen.better.property.Property;
import com.mercateo.common.rest.schemagen.better.property.PropertyBuilder;
import org.junit.Before;
import org.junit.Test;

public class PropertyBuilderTest {

    private PropertyBuilder propertyBuilder;

    @Before
    public void setUp() throws Exception {
        propertyBuilder = new PropertyBuilder(Arrays.asList(new FieldCollector(FieldCollectorConfig.builder().build()), new MethodCollector()));
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

        final Annotation firstElement = getFirstElement(property.annotations().values());
        assertThat(firstElement).isInstanceOf(Annotation1.class);
    }

    @Test
    public void subPropertyReturnsFieldAnnotation() throws Exception {
        Property property = propertyBuilder.from(TwoLevelPropertyHolder.class);

        final Property firstElement1 = getFirstElement(property.children());
        assertThat(getAnnotations(firstElement1)).containsExactly(Annotation1.class, Annotation2.class);
    }

    private Set<Class<? extends Annotation>> getAnnotations(Property firstElement1) {
        return firstElement1.annotations().keySet();
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

    @Test
    public void returnsIdentialTypeDescriptorsForSameType() throws Exception {
        Property property1 = propertyBuilder.from(PropertyHolder.class);
        Property property2 = propertyBuilder.from(PropertyHolder.class);

        assertThat(property1).isNotSameAs(property2);
        assertThat(property1.propertyDescriptor()).isSameAs(property2.propertyDescriptor());
    }

    @Test
    public void returnMethodProperty() throws Exception {
        Property property = propertyBuilder.from(MethodPropertyHolder.class);
        final Property firstElement = getFirstElement(property.children());
        assertThat(firstElement.name()).isEqualTo("property");
    }

    @Test
    public void returnMethodPropertyValue() throws Exception {
        Property property = propertyBuilder.from(MethodPropertyHolder.class);
        final Property firstElement = getFirstElement(property.children());

        final MethodPropertyHolder methodPropertyHolder = new MethodPropertyHolder();
        assertThat(firstElement.getValue(methodPropertyHolder)).isEqualTo("foo");
    }

    @Test
    public void returnMethodAnnotations() throws Exception {
        Property property = propertyBuilder.from(MethodPropertyHolder.class);
        final Property firstElement = getFirstElement(property.children());

        assertThat(getAnnotations(firstElement)).containsExactly(Annotation2.class);
    }

    @Test
    public void returnMethodAndClassAnnotations() throws Exception {
        Property property = propertyBuilder.from(TwoLevelMethodPropertyHolder.class);
        final Property firstElement = getFirstElement(property.children());

        assertThat(getAnnotations(firstElement)).containsExactlyInAnyOrder(Annotation1.class, Annotation3.class);
    }

    public static <T> T getFirstElement(Collection<T> collection) {
        return collection.iterator().next();
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Annotation1 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Annotation2 {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface Annotation3 {
    }

    @Annotation1
    static class PropertyHolder {
        String property;
    }

    @Annotation1
    static class MethodPropertyHolder {
        @Annotation2
        String getProperty() {
            return "foo";
        }
    }

    static class TwoLevelMethodPropertyHolder {
        @Annotation3
        MethodPropertyHolder holder;
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