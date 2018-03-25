package com.code4people.jsonrpclib.binding.info;

import java.lang.reflect.Method;
import java.util.Map;

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
