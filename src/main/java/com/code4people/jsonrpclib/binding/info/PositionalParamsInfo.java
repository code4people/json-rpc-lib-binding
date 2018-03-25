package com.code4people.jsonrpclib.binding.info;

import com.code4people.jsonrpclib.binding.BindingErrorException;
import com.code4people.jsonrpclib.binding.annotations.Optional;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PositionalParamsInfo {

    private final List<Type> parameterTypes;
    private final int numberOfMandatoryParams;

    public static PositionalParamsInfo create(Parameter[] parameters, String methodName) throws BindingErrorException {

        int optionalParamsCount = 0;
        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(Optional.class)) {
                optionalParamsCount++;
            } else if (optionalParamsCount > 0) {
                String message = "@Optional cannot be followed by mandatory param for @Bind that is of POSITIONAL paramsTypes. Method: %s";
                throw new BindingErrorException(String.format(message, methodName));
            }
        }

        List<Type> parameterTypes = Arrays.stream(parameters)
                .map(Parameter::getParameterizedType)
                .collect(Collectors.toList());
        int numberOfMandatoryParams = parameters.length - optionalParamsCount;

        return new PositionalParamsInfo(parameterTypes, numberOfMandatoryParams);
    }

    private PositionalParamsInfo(List<Type> parameterTypes, int numberOfMandatoryParams) throws BindingErrorException {
        this.parameterTypes = parameterTypes;
        this.numberOfMandatoryParams = numberOfMandatoryParams;
    }

    public List<Type> getParameterTypes() {
        return parameterTypes;
    }

    public int getNumberOfMandatoryParams() {
        return numberOfMandatoryParams;
    }
}
