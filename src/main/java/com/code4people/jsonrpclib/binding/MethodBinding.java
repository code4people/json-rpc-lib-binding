package com.code4people.jsonrpclib.binding;

import com.code4people.jsonrpclib.binding.info.MethodInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MethodBinding<T extends MethodInfo> {
    private final T methodInfo;
    private final Supplier<?> receiverSupplier;

    private MethodBinding(T methodInfo, Supplier<?> receiverSupplier) {
        this.methodInfo = methodInfo;
        this.receiverSupplier = receiverSupplier;
    }

    public T getMethodInfo() {
        return methodInfo;
    }

    public Supplier<?> getReceiverSupplier() {
        return receiverSupplier;
    }

    public static class Builder {
        private final Map<Class<?>, Supplier<?>> receivers = new HashMap<>();

        public <T> Builder addReceiver(Class<T> receiver, Supplier<? extends T> supplier) {
            Objects.requireNonNull(receiver, "'receiver' cannot be null");
            Objects.requireNonNull(supplier, "'supplier' cannot be null");
            receivers.put(receiver, supplier);
            return this;
        }

        public List<MethodBinding<? extends MethodInfo>> build() {
            return receivers
                    .entrySet()
                    .stream()
                    .flatMap(cse -> MethodInfo.createFromClass(cse.getKey())
                            .stream()
                            .map(mi -> new MethodBinding<>(mi, cse.getValue())))
                    .collect(Collectors.toList());
        }
    }
}
