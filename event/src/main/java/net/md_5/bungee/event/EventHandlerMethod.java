package net.md_5.bungee.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;

@AllArgsConstructor
public class EventHandlerMethod {

    @Getter
    private final Object listener;
    @Getter
    private final Method method;
    private final Consumer<Object> invoker;

    public EventHandlerMethod(Object listener, Method method) {
        this.listener = listener;
        this.method = method;
        this.invoker = getInvoker(listener, method);
    }

    @SuppressWarnings("unchecked")
    private static Consumer<Object> getInvoker(final Object listener, final Method method) {
        try {
            final MethodHandles.Lookup lookup = getLookup(method.getDeclaringClass());
            final MethodHandle handle = lookup.unreflect(method).bindTo(listener);
            final CallSite callSite = LambdaMetafactory.metafactory(
                    lookup,
                    "accept",
                    MethodType.methodType(Consumer.class),
                    MethodType.methodType(void.class, Object.class),
                    handle,
                    handle.type());
            return (Consumer<Object>) callSite.getTarget().invokeWithArguments();
        } catch (Throwable t) {
            try {
                return event -> {
                    try {
                        getLookup(method.getDeclaringClass()).unreflect(method).bindTo(listener).invokeWithArguments(event);
                    } catch (Throwable t2) {
                        throw new InvokerException(t2);
                    }
                };
            } catch (Throwable t2) {
                return event -> {
                    try {
                        method.invoke(listener, event);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new InvokerException(e);
                    }
                };
            }
        }

    }

    private static MethodHandles.Lookup getLookup(final Class<?> targetClass) {
        try {
            final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                    .getDeclaredConstructor(Class.class);
            return constructor.newInstance(targetClass);
        } catch (Exception e) {
            return MethodHandles.lookup();
        }
    }

    public void invoke(final Object event)
            throws IllegalArgumentException, InvocationTargetException {
        try {
            invoker.accept(event);
        } catch (InvokerException e) {
            throw new InvocationTargetException(e.getCause());
        }
    }

    private static class InvokerException extends RuntimeException {
        InvokerException(final Throwable cause) {
            super(cause);
        }
    }
}
