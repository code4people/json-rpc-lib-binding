package com.code4people.jsonrpclib.binding.info;

import java.lang.reflect.Method;
import java.util.Map;

public class GranularParamsMethodInfo extends MethodInfo {
    private final NamedParamsInfo namedParamsInfo;
    private final PositionalParamsInfo positionalParamsInfo;
    private final MissingParamsInfo missingParamsInfo;

    public GranularParamsMethodInfo(
            String publicName,
            Map<Class<? extends Throwable>, ErrorInfo> errorInfos,
            Method method,
            NamedParamsInfo namedParamsInfo,
            PositionalParamsInfo positionalParamsInfo,
            MissingParamsInfo missingParamsInfo) {

        super(publicName, errorInfos, method);
        this.namedParamsInfo = namedParamsInfo;
        this.positionalParamsInfo = positionalParamsInfo;
        this.missingParamsInfo = missingParamsInfo;
    }

    public NamedParamsInfo getNamedParamsInfo() {
        return namedParamsInfo;
    }

    public PositionalParamsInfo getPositionalParamsInfo() {
        return positionalParamsInfo;
    }

    public MissingParamsInfo getMissingParamsInfo() {
        return missingParamsInfo;
    }
}
