package com.code4people.jsonrpclib.binding.factories;

import com.code4people.jsonrpclib.binding.BindingErrorException;
import com.code4people.jsonrpclib.binding.annotations.Bind;
import com.code4people.jsonrpclib.binding.annotations.BindToSingleArgument;
import com.code4people.jsonrpclib.binding.annotations.Error;
import com.code4people.jsonrpclib.binding.annotations.ErrorMapping;
import com.code4people.jsonrpclib.binding.info.ErrorInfo;
import com.code4people.jsonrpclib.binding.info.MethodInfo;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MethodInfoFactory {

    private final GranularParamsMethodInfoFactory granularParamsMethodInfoFactory;
    private final SingleArgumentMethodInfoFactory singleArgumentMethodInfoFactory;

    public MethodInfoFactory(GranularParamsMethodInfoFactory granularParamsMethodInfoFactory, SingleArgumentMethodInfoFactory singleArgumentMethodInfoFactory) {
        this.granularParamsMethodInfoFactory = granularParamsMethodInfoFactory;
        this.singleArgumentMethodInfoFactory = singleArgumentMethodInfoFactory;
    }

    public List<? extends MethodInfo> createFromClass(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Bind.class)
                        || m.isAnnotationPresent(BindToSingleArgument.class))
                .map(this::createFromMethod)
                .collect(Collectors.toList());
    }

    public MethodInfo createFromMethod(Method method) {
        ErrorMapping errorMapping = method.getAnnotation(ErrorMapping.class);
        Error[] errors = errorMapping == null
                ? new Error[0]
                : errorMapping.value();
        if (Arrays.stream(errors).map(Error::exception).distinct().count() != errors.length) {
            String message = String.format("Method '%s' has duplicated exceptions mapping.", method.toString());
            throw new BindingErrorException(message);
        }

        List<Error> invalidErrors = Arrays.stream(errors)
                .filter(error -> !Throwable.class.isAssignableFrom(error.exception()))
                .collect(Collectors.toList());

        if (!invalidErrors.isEmpty()) {
            throw new BindingErrorException("Invalid error mappings: " + invalidErrors);
        }

        Map<Class<? extends Throwable>, ErrorInfo> errorInfos =
                Arrays.stream(errors)
                        .collect(Collectors.toMap(Error::exception, e -> new ErrorInfo(e.code(), e.message())));

        if (method.isAnnotationPresent(Bind.class)) {
            return granularParamsMethodInfoFactory.create(method, errorInfos);
        }
        else if (method.isAnnotationPresent(BindToSingleArgument.class)) {
            return singleArgumentMethodInfoFactory.create(method, errorInfos);
        }
        else {
            throw new IllegalArgumentException("No annotation exposing method is present.");
        }
    }
}
