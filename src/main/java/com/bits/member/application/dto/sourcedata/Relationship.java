package com.bits.member.application.dto.sourcedata;

public record Relationship(
        String id,
        String name,
        Boolean relative,
        Boolean spouseRelationship,
        Integer statusId,
        Boolean active) {
}
