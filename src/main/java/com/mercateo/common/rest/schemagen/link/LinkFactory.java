package com.mercateo.common.rest.schemagen.link;

import com.mercateo.common.rest.schemagen.JerseyResource;
import com.mercateo.common.rest.schemagen.link.helper.InvocationRecorder;
import com.mercateo.common.rest.schemagen.link.helper.InvocationRecordingResult;
import com.mercateo.common.rest.schemagen.link.helper.MethodInvocation;
import com.mercateo.common.rest.schemagen.link.helper.ProxyFactory;
import com.mercateo.common.rest.schemagen.link.relation.Relation;
import com.mercateo.common.rest.schemagen.link.relation.RelationContainer;
import com.mercateo.common.rest.schemagen.parameter.CallContext;
import com.mercateo.common.rest.schemagen.parameter.Parameter;

import javax.ws.rs.core.Link;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LinkFactory<T extends JerseyResource> {

    private final List<Scope> scopes;

    private final LinkFactoryContext context;

    private final LinkCreator linkCreator;

    private final T resourceProxy;

    LinkFactory(Class<T> resourceClass, LinkFactoryContext context, List<Scope> scopes) {
        this.resourceProxy = ProxyFactory.createProxy(resourceClass);
        this.context = context;
        this.scopes = scopes != null ? scopes : new ArrayList<>();
        this.linkCreator = new LinkCreator(context);
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param rel              relation of link
     * @param methodInvocation lambda with function call
     * @return the specified link or absent, if the user has not the required
     * permission to call the resource!
     */
    public Optional<Link> forCall(URI baseUri, RelationContainer rel, MethodInvocation<T> methodInvocation) {
        return forCall(baseUri, rel.getRelation(), methodInvocation);
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param rel              relation of link
     * @param methodInvocation lambda with function call
     * @return the specified link or absent, if the user has not the required
     * permission to call the resource!
     * @deprecated use {{@link #forCall(URI, RelationContainer, MethodInvocation)}} instead
     */
    @Deprecated
    public Optional<Link> forCall(RelationContainer rel, MethodInvocation<T> methodInvocation) {
        return forCall(rel.getRelation(), methodInvocation);
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param rel              relation of link
     * @param methodInvocation lambda with function call
     * @param callContext      parameter container containing information about allowed and
     *                         default values
     * @return the specified link or absent, if the user has not the required
     * permission to call the resource!
     */
    public Optional<Link> forCall(URI baseUri, RelationContainer rel, MethodInvocation<T> methodInvocation,
                                  CallContext callContext) {
        return forCall(baseUri, rel.getRelation(), methodInvocation, callContext);
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param rel              relation of link
     * @param methodInvocation lambda with function call
     * @param callContext      parameter container containing information about allowed and
     *                         default values
     * @return the specified link or absent, if the user has not the required
     * permission to call the resource!
     * @deprecated use {{@link #forCall(URI, RelationContainer, MethodInvocation, CallContext)}} instead
     */
    @Deprecated
    public Optional<Link> forCall(RelationContainer rel, MethodInvocation<T> methodInvocation,
                                  CallContext callContext) {
        return forCall(rel.getRelation(), methodInvocation, callContext);
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param relation         relation of link
     * @param methodInvocation lambda with function call
     * @return the specified link or absent, if the user has not the required
     * permission to call the resource!
     */
    public Optional<Link> forCall(URI baseUri, Relation relation, MethodInvocation<T> methodInvocation) {
        return forCall(baseUri, relation, methodInvocation, Parameter.createContext());
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param relation         relation of link
     * @param methodInvocation lambda with function call
     * @return the specified link or absent, if the user has not the required
     * permission to call the resource!
     * @deprecated use {{@link #forCall(URI, Relation, MethodInvocation)}} instead
     */
    @Deprecated
    public Optional<Link> forCall(Relation relation, MethodInvocation<T> methodInvocation) {
        return forCall(relation, methodInvocation, Parameter.createContext());
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param relation         relation of link
     * @param methodInvocation lambda with function call
     * @param callContext      parameter container containing information about allowed and
     *                         default values
     * @return the specified link or absent, if the user has not the required
     * permission to call the resource!
     * @deprecated use {{@link #forCall(URI, Relation, MethodInvocation, CallContext)}} instead
     */
    @Deprecated
    public Optional<Link> forCall(Relation relation, MethodInvocation<T> methodInvocation,
                                  CallContext callContext) {
        final List<Scope> scopes = createScopes(methodInvocation, callContext);

        if (scopes.stream().allMatch(this::hasAllPermissions)) {
            return Optional.of(linkCreator.createFor(scopes, relation));
        }
        return Optional.empty();
    }

    /**
     * computes the link for the resource method, which is called the last time
     * in the lambda. Please note, that the method has to be non final!
     *
     * @param relation         relation of link
     * @param methodInvocation lambda with function call
     * @param callContext      parameter container containing information about allowed and
     *                         default values
     * @return the specified link or absent, if the user has not the required
     * permission to call the resource!
     */
    public Optional<Link> forCall(URI baseUri, Relation relation, MethodInvocation<T> methodInvocation,
                                  CallContext callContext) {
        final List<Scope> scopes = createScopes(methodInvocation, callContext);

        if (scopes.stream().allMatch(this::hasAllPermissions)) {
            return Optional.of(linkCreator.createFor(baseUri, scopes, relation));
        }
        return Optional.empty();
    }

    private List<Scope> createScopes(MethodInvocation<T> methodInvocation, CallContext callContext) {
        final List<Scope> scopes = new ArrayList<>(this.scopes);
        scopes.add(createCallScope(methodInvocation, callContext));
        return scopes;
    }

    public <U extends JerseyResource> LinkFactory<U> subResource(
            MethodInvocation<T> methodInvocation, Class<U> subResourceClass) {
        return new LinkFactory<>(subResourceClass, context, new ArrayList<Scope>(scopes) {
            private static final long serialVersionUID = -9144825887185625018L;

            {
                add(createSubresourceScope(methodInvocation));
            }
        });
    }

    private Scope createCallScope(MethodInvocation<T> methodInvocation, CallContext callContext) {
        final InvocationRecordingResult result = recordMethodCall(methodInvocation);
        return new CallScope(result.getInvokedClass(), result.getMethod(), result.getParams(), callContext);
    }

    private Scope createSubresourceScope(MethodInvocation<T> methodInvocation) {
        final InvocationRecordingResult result = recordMethodCall(methodInvocation);
        return new SubResourceScope(result.getInvokedClass(), result.getMethod(), result.getParams());
    }

    private InvocationRecordingResult recordMethodCall(MethodInvocation<T> methodInvocation) {
        final InvocationRecordingResult result;
        synchronized (resourceProxy) {
            methodInvocation.record(resourceProxy);
            result = ((InvocationRecorder) resourceProxy).getInvocationRecordingResult();
        }
        return result;
    }

    private boolean hasAllPermissions(Scope scope) {
        return context == null || context.getMethodCheckerForLink().test(scope);
    }

}
