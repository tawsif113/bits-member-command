package com.bits.member.domain.event;

import com.bits.ddd.shared.localization.LocalizedMessage;
import com.bits.ddd.shared.messaging.FailureMessage;
import com.bits.member.domain.enums.MemberErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;

public class MemberFailedEvent extends FailureMessage {

    @JsonIgnore
    public static final String TOPIC_EXCHANGE = "member.error.exchange";
    @JsonIgnore
    public static final String ROUTING_KEY = "member.failed";

    private MemberFailedEvent(String tracerId, MemberErrorCode errorCode, Map<String, LocalizedMessage> errorMap) {
        super(tracerId, errorCode, errorMap);
        initRouting(TOPIC_EXCHANGE, ROUTING_KEY);
    }

    public static FailureMessage validationError(String tracerId, Map<String, LocalizedMessage> errors) {
        return new MemberFailedEvent(tracerId, MemberErrorCode.MEMBER_VALIDATION_FAILED, errors);
    }

    public static FailureMessage sourceDataError(String tracerId, Map<String, LocalizedMessage> errors) {
        return new MemberFailedEvent(tracerId, MemberErrorCode.SOURCE_DATA_ERROR, errors);
    }
}
