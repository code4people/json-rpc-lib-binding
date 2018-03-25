package com.code4people.jsonrpclib.binding.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.code4people.jsonrpclib.binding.annotations.ParamsType.DEFAULT;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(METHOD)
@Retention(RUNTIME)
public @interface Bind {
    String as() default "";
    ParamsType paramsType() default DEFAULT;
}
