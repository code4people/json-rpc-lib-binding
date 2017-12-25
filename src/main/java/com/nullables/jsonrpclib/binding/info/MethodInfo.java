package com.nullables.jsonrpclib.binding.info;

import com.nullables.jsonrpclib.binding.BindingErrorException;
import com.nullables.jsonrpclib.binding.annotations.Bind;
import com.nullables.jsonrpclib.binding.annotations.BindToSingleArgument;
import com.nullables.jsonrpclib.binding.annotations.ErrorMapping;
import com.nullables.jsonrpclib.binding.annotations.ParamsType;
import com.nullables.jsonrpclib.binding.annotations.Error;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class MethodInfo {
    private final String publicName;
    private final Map<Class<? extends Throwable>, ErrorInfo> errorInfos;
    private final Method method;

    public MethodInfo(String publicName,
                      Map<Class<? extends Throwable>, ErrorInfo> errorInfos,
                      Method method) {

        this.publicName = publicName;
        this.errorInfos = errorInfos;
        this.method = method;
    }

    public static List<? extends MethodInfo> createFromClass(Class<?> clazz) throws BindingErrorException {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Bind.class)
                        || m.isAnnotationPresent(BindToSingleArgument.class))
                .map(MethodInfo::createFromMethod)
                .collect(Collectors.toList());
    }

    public static MethodInfo createFromMethod(Method method) throws BindingErrorException {

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
            return createGranularParamsMethodBinding(method, errorInfos);
        }
        else if (method.isAnnotationPresent(BindToSingleArgument.class)) {
            return createSingleArgumentMethodBinding(method, errorInfos);
        }
        else {
            throw new IllegalArgumentException("No annotation exposing method is present.");
        }
    }

    private static GranularParamsMethodInfo createGranularParamsMethodBinding(
            Method method,
            Map<Class<? extends Throwable>, ErrorInfo> errorInfos) throws BindingErrorException {

        Bind annotation = method.getAnnotation(Bind.class);
        String publicName = annotation.as();
        if (publicName.isEmpty()) {
            publicName = method.getName();
        }

        EnumSet<ParamsType> paramsTypes = EnumSet.copyOf(Arrays.asList(annotation.paramsTypes()));
        NamedParamsInfo namedParamsInfo = paramsTypes.contains(ParamsType.NAMED)
                ? NamedParamsInfo.create(method)
                : null;
        PositionalParamsInfo positionalParamsInfo = paramsTypes.contains(ParamsType.POSITIONAL)
                ? PositionalParamsInfo.create(method)
                : null;
        MissingParamsInfo missingParamsInfo = paramsTypes.contains(ParamsType.MISSING)
                ? MissingParamsInfo.create(method.getParameterCount())
                : null;

        return new GranularParamsMethodInfo(
                publicName,
                errorInfos,
                method,
                namedParamsInfo,
                positionalParamsInfo,
                missingParamsInfo);
    }

    private static SingleArgumentMethodInfo createSingleArgumentMethodBinding(
            Method method,
            Map<Class<? extends Throwable>, ErrorInfo> errorInfos) throws BindingErrorException {

        BindToSingleArgument annotation = method.getAnnotation(BindToSingleArgument.class);
        String publicName = annotation.as();
        if (publicName.isEmpty()) {
            publicName = method.getName();
        }

        return new SingleArgumentMethodInfo(
                publicName,
                errorInfos,
                method);
    }

    public String getPublicName() {
        return publicName;
    }

    public Map<Class<? extends Throwable>, ErrorInfo> getErrorInfos() {
        return errorInfos;
    }

    public Method getMethod() {
        return method;
    }

}
