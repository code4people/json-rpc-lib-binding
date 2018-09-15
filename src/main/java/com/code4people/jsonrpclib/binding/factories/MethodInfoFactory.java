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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MethodInfoFactory {

    private final GranularParamsMethodInfoFactory granularParamsMethodInfoFactory;
    private final SingleArgumentMethodInfoFactory singleArgumentMethodInfoFactory;

    public MethodInfoFactory(GranularParamsMethodInfoFactory granularParamsMethodInfoFactory, SingleArgumentMethodInfoFactory singleArgumentMethodInfoFactory) {
        this.granularParamsMethodInfoFactory = granularParamsMethodInfoFactory;
        this.singleArgumentMethodInfoFactory = singleArgumentMethodInfoFactory;
    }

    public List<? extends MethodInfo> createFromClass(Class<?> clazz) {
        ErrorMapping errorMapping = clazz.getAnnotation(ErrorMapping.class);
        Error[] classErrors = errorMapping == null
                ? new Error[0]
                : errorMapping.value();
        if (Arrays.stream(classErrors).map(Error::exception).distinct().count() != classErrors.length) {
            String message = String.format("Class '%s' has duplicated exceptions mapping.", clazz.toString());
            throw new BindingErrorException(message);
        }

        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Bind.class)
                        || m.isAnnotationPresent(BindToSingleArgument.class))
                .map(m -> createFromMethod(m, classErrors))
                .collect(Collectors.toList());
    }

    public MethodInfo createFromMethod(Method method, Error[] classErrors) {
        ErrorMapping errorMapping = method.getAnnotation(ErrorMapping.class);
        Error[] methodErrors = errorMapping == null
                ? new Error[0]
                : errorMapping.value();
        if (Arrays.stream(methodErrors).map(Error::exception).distinct().count() != methodErrors.length) {
            String message = String.format("Method '%s' has duplicated exceptions mapping.", method.toString());
            throw new BindingErrorException(message);
        }

        Map<Class<? extends Throwable>, ErrorInfo> errorsMap = new HashMap<>();
        Stream.concat(Arrays.stream(classErrors), Arrays.stream(methodErrors))
                .forEach(e -> errorsMap.put(e.exception(), new ErrorInfo(e.code(), e.message())));

        if (method.isAnnotationPresent(Bind.class)) {
            return granularParamsMethodInfoFactory.create(method, errorsMap);
        }
        else if (method.isAnnotationPresent(BindToSingleArgument.class)) {
            return singleArgumentMethodInfoFactory.create(method, errorsMap);
        }
        else {
            throw new IllegalArgumentException("No annotation exposing method is present.");
        }
    }
}
