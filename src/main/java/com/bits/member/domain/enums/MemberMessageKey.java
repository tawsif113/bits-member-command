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
    TARGET_AMOUNT("targetAmount"),
    MEMBER_INACTIVE("member.inactive"),
    MEMBER_CLOSED("member.closed"),
    MEMBER_DELETE_UNAUTHORIZED("member.delete.unauthorized"),
    MEMBER_HAS_LOAN_PROPOSALS("member.has.loan.proposals"),
    MEMBER_HAS_SAVINGS("member.has.savings"),
    MEMBER_NOT_INACTIVE("member.not.inactive"),
    NO_DATA_TO_SAVE("member.no.data"),
    OFFICE("office"),
    APP_DATE("applicationDate"),
    DOB("dateOfBirth"),
    CATEGORY("category"),
    GROUP("group"),
    PO("projectOfficer"),
    SAVINGS_PRODUCT("savingsProduct"),
    SMART_CARD("smartCardId"),
    OTHER_ID("otherIdNo"),
    SPOUSE_NID("spouseNationalId"),
    DEDUPE("deduplication"),
    NOMINEE("nominee"),
    GUARDIAN("guardian"),
    GUARDIAN_NID("guardianNationalId"),
    GUARANTOR_NID("guarantorNationalId"),
    GUARANTOR_REL("guarantorRelationship"),
    IDENTITY_CONFLICT("identityConflict"),
    BANK("bankInfo"),
    PASSBOOK("passbook");

    private final String key;

    MemberMessageKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
