package com.bits.member.domain.enums;

import com.bits.ddd.shared.exception.ErrorCodeProvider;

public enum MemberErrorCode implements ErrorCodeProvider {
    MEMBER_VALIDATION_FAILED("MEMBER_VALIDATION_FAILED", "Member validation failed"),
    SOURCE_DATA_ERROR("SOURCE_DATA_ERROR", "Member source data loading failed"),
    MEMBER_CREATION_LOCKED("MEMBER_CREATION_LOCKED", "Member creation is already in progress");

    private final String code;
    private final String message;

    MemberErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
