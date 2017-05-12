package com.mercateo.rest.schemagen;

import com.mercateo.reflection.Call;
import com.mercateo.rest.schemagen.link.CallScope;
import com.mercateo.rest.schemagen.plugin.FieldCheckerForSchema;
import org.junit.Test;

import javax.ws.rs.BeanParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class SchemaGeneratorFacadeTest {

    private SchemaGeneratorFacade schemaGenerator;

    private FieldCheckerForSchema fieldCheckerForSchema;

    @Test
    public void createInputSchema() throws NoSuchMethodException {
        final Method getStrings = Call.of(TestResource.class, r -> r.getStrings(0, 0)).method();
        final Optional<String> inputSchema = schemaGenerator.createInputSchema(new CallScope(TestResource.class,
                getStrings, new Object[0], null) {
        }, fieldCheckerForSchema);
        assertThat(inputSchema.isPresent()).isFalse();
    }

    @Path("/home")
    public class TestResource {

        @GET
        @Path("/at")
        public String[] getStrings(@QueryParam("offset") int offset, @QueryParam("limit") int limit) {
            return null;
        }

        @PUT
        @Path("/foo")
        public void setValue(String name, @QueryParam("debug") boolean debug) {
            // Nothing to do.
        }

        @POST
        @Path("/bar")
        public void setName(@FormParam("firstName") String firstName, @FormParam("lastName") String lastName) {
            // Nothing to do.
        }

        @PUT
        @Path("/of")
        public void paramBean(@BeanParam TestBeanParam beanParam) {
            // Nothing to do.
        }

        @PUT
        @Path("/ofContext")
        public void context(@Context String bla) {
            // Nothing to do.
        }

        @GET
        @Path("/enumValue")
        public void enumValue(TestEnum enumValue) {
            // Nothing to do
        }

        @GET
        @Path("/enumValueJsonValue")
        public void enumValueJsonValue(TestEnumJsonValue enumValue) {
            // Nothing to do
        }

        @GET
        @Path("/media")
        public void media(@Media(type = "<type>", binaryEncoding = "<binaryEncoding>") String media) {
        }
    }

    public static class TestBeanParam {
        @DefaultValue("5")
        @QueryParam("size")
        private final Integer size;

        @QueryParam("fields")
        private List<String> fields;

        @PathParam("pathParam")
        private String pathParam;

        public TestBeanParam(List<String> fields, int size) {
            this.fields = fields;
            this.size = size;
        }
    }
}