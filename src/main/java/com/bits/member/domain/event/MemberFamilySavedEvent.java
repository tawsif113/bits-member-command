package com.bits.member.domain.event;

import com.bits.ddd.shared.messaging.DomainEventMessage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberFamilySavedEvent extends DomainEventMessage {

    @JsonIgnore
    public static final String TOPIC_EXCHANGE = "member.event.exchange";
    @JsonIgnore
    public static final String ROUTING_KEY = "member.family.saved";

    private String memberId;
    private String memberNo;
    private Integer nomineeCount;
    private Boolean hasGuarantor;

    public MemberFamilySavedEvent(
            String memberId,
            String memberNo,
            Integer nomineeCount,
            Boolean hasGuarantor,
            long version,
            String tracerId) {
        super(memberId, "Member", version, tracerId, TOPIC_EXCHANGE, ROUTING_KEY);
        this.memberId = memberId;
        this.memberNo = memberNo;
        this.nomineeCount = nomineeCount;
        this.hasGuarantor = hasGuarantor;
    }
}
