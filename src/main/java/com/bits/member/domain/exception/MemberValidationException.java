package com.bits.member.domain.exception;

import com.bits.ddd.shared.exception.domain.FailureException;
import com.bits.ddd.shared.messaging.FailureMessage;

public class MemberValidationException extends FailureException {

    public MemberValidationException(FailureMessage failureEvent) {
        super(failureEvent);
    }
}
