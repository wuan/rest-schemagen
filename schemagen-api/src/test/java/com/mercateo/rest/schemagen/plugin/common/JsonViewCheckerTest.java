package com.mercateo.rest.schemagen.plugin.common;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.common.collect.Sets;
import com.mercateo.rest.schemagen.PropertyContext;

public class JsonViewCheckerTest {

    public static class TestBean {
        @JsonView({ Class.class, TestBean.class })
        private String viewField;

        public String getViewField() {
            return viewField;
        }

        public void setViewField(String viewField) {
            this.viewField = viewField;
        }
    }

    @Test
    public void test_true_without_views_in_context() throws NoSuchFieldException,
            SecurityException {
        PropertyContext propertyContext = Mockito.mock(PropertyContext.class);
        Mockito.when(propertyContext.getViewClasses()).thenReturn(Collections.emptySet());
        JsonViewChecker uut = new JsonViewChecker();
        Assert.assertTrue(uut.test(TestBean.class.getDeclaredField("viewField"), propertyContext));
    }

    @Test
    public void test_true_with_views_in_context() throws NoSuchFieldException, SecurityException {
        PropertyContext propertyContext = Mockito.mock(PropertyContext.class);
        Mockito.when(propertyContext.getViewClasses()).thenReturn(Sets.newHashSet(Class.class, this
                .getClass()));
        JsonViewChecker uut = new JsonViewChecker();
        Assert.assertTrue(uut.test(TestBean.class.getDeclaredField("viewField"), propertyContext));
    }

    @Test
    public void test_false_with_views_in_context() throws NoSuchFieldException, SecurityException {
        PropertyContext propertyContext = Mockito.mock(PropertyContext.class);
        Mockito.when(propertyContext.getViewClasses()).thenReturn(Sets.newHashSet(this.getClass()));
        JsonViewChecker uut = new JsonViewChecker();
        Assert.assertFalse(uut.test(TestBean.class.getDeclaredField("viewField"), propertyContext));
    }
}
