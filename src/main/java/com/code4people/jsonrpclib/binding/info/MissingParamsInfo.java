package com.code4people.jsonrpclib.binding.info;

import com.code4people.jsonrpclib.binding.BindingErrorException;

public class MissingParamsInfo {

    private final int numberOfOptionalParams;

    public static MissingParamsInfo create(int numberOfOptionalParams) throws BindingErrorException {
        return new MissingParamsInfo(numberOfOptionalParams);
    }

    private MissingParamsInfo(int numberOfOptionalParams) throws BindingErrorException {
        this.numberOfOptionalParams = numberOfOptionalParams;
    }

    public int getNumberOfOptionalParams() {
        return numberOfOptionalParams;
    }
}
