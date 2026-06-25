package com.bits.member.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PassbookInfo(
        String id,
        String passbookNo,
        LocalDate issueDate,
        String passbookStatusId,
        BigDecimal passbookPrice,
        LocalDate statusChangeDate,
        String transactionNo) {
}
