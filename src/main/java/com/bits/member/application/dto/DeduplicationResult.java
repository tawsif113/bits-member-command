package com.bits.member.application.dto;

public record DeduplicationResult(
        boolean failed,
        String errorDetail,
        boolean nationalIdDuplicate,
        boolean smartCardDuplicate,
        boolean otherIdDuplicate,
        boolean passportDuplicate,
        boolean spouseNidDuplicate,
        boolean generalDuplicate) {

    public static DeduplicationResult notChecked() {
        return new DeduplicationResult(false, null, false, false, false, false, false, false);
    }
}
