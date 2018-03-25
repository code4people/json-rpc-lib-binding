package com.code4people.jsonrpclib.binding.factories;

import com.code4people.jsonrpclib.binding.BindingErrorException;
import com.code4people.jsonrpclib.binding.annotations.Bind;
import com.code4people.jsonrpclib.binding.annotations.Optional;
import com.code4people.jsonrpclib.binding.annotations.Param;
import com.code4people.jsonrpclib.binding.annotations.ParamsType;
import com.code4people.jsonrpclib.binding.info.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;

import static com.code4people.jsonrpclib.binding.annotations.ParamsType.DEFAULT;
import static com.code4people.jsonrpclib.binding.annotations.ParamsType.NAMED;
import static com.code4people.jsonrpclib.binding.annotations.ParamsType.POSITIONAL;

public class GranularParamsMethodInfoFactory {
    public GranularParamsMethodInfo create(Method method, Map<Class<? extends Throwable>, ErrorInfo> errorInfos) {
        Bind annotation = method.getAnnotation(Bind.class);
        String publicName = annotation.as();
        if (publicName.isEmpty()) {
            publicName = method.getName();
        }

        Parameter[] parameters = method.getParameters();
        ParamsType paramsType = annotation.paramsType();

        boolean isMissingParams = Arrays.stream(parameters).allMatch(p -> p.isAnnotationPresent(Optional.class));
        boolean isNamedParams = paramsType == NAMED
                || (paramsType == DEFAULT && (parameters.length == 0 || Arrays.stream(parameters).anyMatch(p -> p.isAnnotationPresent(Param.class))));
        boolean isPositionalParams = EnumSet.of(POSITIONAL, DEFAULT).contains(paramsType);

        NamedParamsInfo namedParamsInfo = isNamedParams
                ? NamedParamsInfo.create(parameters, method.getName())
                : null;
        PositionalParamsInfo positionalParamsInfo = isPositionalParams
                ? PositionalParamsInfo.create(parameters, method.getName())
                : null;
        MissingParamsInfo missingParamsInfo = isMissingParams
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
}
