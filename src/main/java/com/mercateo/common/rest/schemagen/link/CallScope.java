package com.mercateo.common.rest.schemagen.link;

import java.lang.reflect.Method;
import java.util.Optional;

import com.mercateo.common.rest.schemagen.parameter.CallContext;

public class CallScope extends Scope {
    private final Optional<CallContext> callContext;

    public CallScope(Class<?> clazz, Method method, Object[] params, CallContext callContext) {
        super(clazz, method, params);
        this.callContext = Optional.ofNullable(callContext);
    }

    public Optional<CallContext> getCallContext() {
        return callContext;
    }

    @Override
    public String toString() {
        return "CallScope{" + "class=" + getInvokedClass().getSimpleName() + ", " + "method=" + getInvokedMethod()
                .getName() + callContext.map(c -> ", " + c.toString()).orElse("") + '}';
    }
}
