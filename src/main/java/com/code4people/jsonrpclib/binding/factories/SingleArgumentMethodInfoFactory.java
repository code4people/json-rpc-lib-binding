package com.code4people.jsonrpclib.binding.factories;

import com.code4people.jsonrpclib.binding.annotations.BindToSingleArgument;
import com.code4people.jsonrpclib.binding.info.ErrorInfo;
import com.code4people.jsonrpclib.binding.info.SingleArgumentMethodInfo;

import java.lang.reflect.Method;
import java.util.Map;

public class SingleArgumentMethodInfoFactory {
    public SingleArgumentMethodInfo create(Method method, Map<Class<? extends Throwable>, ErrorInfo> errorInfos) {
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
}
