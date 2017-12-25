package com.nullables.jsonrpclib.binding.info;

public class ErrorInfo {
    public final int code;
    public final String message;

    public ErrorInfo(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
