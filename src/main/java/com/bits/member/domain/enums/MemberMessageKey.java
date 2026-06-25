package com.bits.member.domain.enums;

public enum MemberMessageKey {
    MEMBER("member"),
    NOT_FOUND("member.not.found"),
    VALIDATION_FAILED("member.validation.failed"),
    SOURCE_DATA_ERROR("member.source.data.error"),
    CREATION_LOCKED("member.creation.locked"),
    MEMBER_NAME("memberName"),
    GENDER("gender"),
    DATE_OF_BIRTH("dateOfBirth"),
    NATIONAL_ID("nationalId"),
    BUSINESS_DAY("businessDay"),
    TARGET_AMOUNT("targetAmount");

    private final String key;

    MemberMessageKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
