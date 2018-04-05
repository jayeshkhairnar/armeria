package com.ctrlshift.commons;

import javax.annotation.Nullable;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.InternalThreadLocalMap;

final class RequestContextThreadLocal {

    private static final FastThreadLocal<RequestContext> context = new FastThreadLocal<>();

    @Nullable
    @SuppressWarnings("unchecked")
    static <T extends RequestContext> T get() {
        return (T) context.get();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    static <T extends RequestContext> T getAndSet(RequestContext ctx) {
        final InternalThreadLocalMap map = InternalThreadLocalMap.get();
        final RequestContext oldCtx = context.get(map);
        context.set(map, ctx);
        return (T) oldCtx;
    }

    static void set(RequestContext ctx) {
        context.set(ctx);
    }

    static void remove() {
        context.remove();
    }

    private RequestContextThreadLocal() {}
}
