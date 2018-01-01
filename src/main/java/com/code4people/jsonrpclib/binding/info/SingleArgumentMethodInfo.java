package com.code4people.jsonrpclib.binding.info;

import java.lang.reflect.Method;
import java.util.Map;

public class SingleArgumentMethodInfo extends MethodInfo {

    public SingleArgumentMethodInfo(String publicName,
                                    Map<Class<? extends Throwable>, ErrorInfo> errorInfos,
                                    Method method) {
        super(publicName, errorInfos, method);
    }
}
