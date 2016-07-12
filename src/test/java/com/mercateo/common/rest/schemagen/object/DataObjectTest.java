package com.mercateo.common.rest.schemagen.object;


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

    private DataObject dataObject;

    static class TestData {
        String value;
    }

    static class TestClass {
        String foo;

        int bar;

        TestData baz;
    }

    @Mock
    private FieldCheckerForSchema fieldCheckerForSchema;

    @Before
    public void setUp() {
        final SchemaPropertyContext schemaPropertyContext = new SchemaPropertyContext(CallContext.create(), fieldCheckerForSchema);
        dataObject = new DataObject(GenericType.of(TestClass.class), schemaPropertyContext);
    }

    @Test
    public void shouldMapSimpleClasses() {
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

}