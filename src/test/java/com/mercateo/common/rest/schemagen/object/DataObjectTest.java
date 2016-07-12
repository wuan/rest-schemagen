package com.mercateo.common.rest.schemagen.object;


import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.mercateo.common.rest.schemagen.SchemaPropertyContext;
import com.mercateo.common.rest.schemagen.generictype.GenericType;
import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.common.rest.schemagen.plugin.FieldCheckerForSchema;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DataObjectTest {

    private CallContext callContext;
    private SchemaPropertyContext schemaPropertyContext;

    static class TestData {
        String value;
    }

    static class TestClass {
        String foo;

        int bar;

        TestData baz;
    }

    static class TestWithUnwrappedClass {
        String value;

        @JsonUnwrapped
        TestClass testClass;
    }

    @Mock
    private FieldCheckerForSchema fieldCheckerForSchema;

    @Before
    public void setUp() {
        callContext = CallContext.create();
        schemaPropertyContext = new SchemaPropertyContext(callContext, fieldCheckerForSchema);
    }

    @Test
    public void shouldMapSimpleClasses() {
        DataObject dataObject = new DataObject(GenericType.of(TestClass.class), schemaPropertyContext);
        final Map<String, DataObject> children = dataObject.getChildren();

        assertThat(children.keySet()).containsExactly("bar", "baz", "foo");

        final DataObject bar = children.get("bar");
        assertThat(bar.getChildren()).isEmpty();
        assertThat(bar.getType()).isEqualTo(int.class);

        final DataObject baz = children.get("baz");
        final Map<String, DataObject> bazChildren = baz.getChildren();
        assertThat(bazChildren.keySet()).containsExactly("value");
        assertThat(baz.getType()).isEqualTo(TestData.class);

        final DataObject bazChild = bazChildren.get("value");
        assertThat(bazChild.getChildren()).isEmpty();
        assertThat(bazChild.getType()).isEqualTo(String.class);

        final DataObject foo = children.get("foo");
        assertThat(foo.getChildren()).isEmpty();
        assertThat(foo.getType()).isEqualTo(String.class);
    }

    @Test
    public void shouldMapUnwrappedContent() {
        DataObject dataObject = new DataObject(GenericType.of(TestWithUnwrappedClass.class), schemaPropertyContext);
        final Map<String, DataObject> children = dataObject.getChildren();

        assertThat(children.keySet()).containsExactly("bar", "baz", "foo", "value");
    }

}