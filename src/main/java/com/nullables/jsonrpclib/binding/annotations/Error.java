package com.nullables.jsonrpclib.binding.annotations;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
public @interface Error {
    Class<? extends Throwable> exception();
    int code();
    String message();
}
