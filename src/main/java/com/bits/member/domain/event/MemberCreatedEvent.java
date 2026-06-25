package com.bits.member.domain.event;

import com.bits.ddd.shared.messaging.DomainEventMessage;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberCreatedEvent extends DomainEventMessage {

    @JsonIgnore
    public static final String TOPIC_EXCHANGE = "member.event.exchange";
    @JsonIgnore
    public static final String ROUTING_KEY = "member.created";

    private String memberId;
    private String memberNo;
    private String memberName;
    private String branchInfoId;
    private String projectInfoId;

    public MemberCreatedEvent(
            String memberId,
            String memberNo,
            String memberName,
            String branchInfoId,
            String projectInfoId,
            long version,
            String tracerId) {
        super(memberId, "Member", version, tracerId, TOPIC_EXCHANGE, ROUTING_KEY);
        this.memberId = memberId;
        this.memberNo = memberNo;
        this.memberName = memberName;
        this.branchInfoId = branchInfoId;
        this.projectInfoId = projectInfoId;
    }
}
