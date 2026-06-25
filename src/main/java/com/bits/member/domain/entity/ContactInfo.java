package com.bits.member.domain.entity;

import java.util.List;

public record ContactInfo(
        String contactNo,
        String contactNoOptional,
        List<MemberAddress> addresses) {
}
