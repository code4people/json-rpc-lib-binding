package com.nullables.jsonrpclib.binding.info;

import com.nullables.jsonrpclib.binding.BindingErrorException;
import com.nullables.jsonrpclib.binding.annotations.Optional;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class PositionalParamsInfo {

    private final List<Type> parameterTypes;
    private final int numberOfMandatoryParams;

    public static PositionalParamsInfo create(Method method) throws BindingErrorException {

        Parameter[] parameters = method.getParameters();

        int optionalParamsCount = 0;
        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(Optional.class)) {
                optionalParamsCount++;
            } else if (optionalParamsCount > 0) {
                throw new BindingErrorException("@Optional cannot be followed by mandatory param for @Bind that is of POSITIONAL paramsTypes.");
            }
        }

        List<Type> parameterTypes = Arrays.asList(method.getGenericParameterTypes());
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
