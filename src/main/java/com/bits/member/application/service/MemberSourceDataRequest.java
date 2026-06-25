package com.bits.member.application.service;

import com.bits.member.application.command.CreateMemberCommand;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MemberSourceDataRequest {

    public static final String PHYSICAL_OFFICE = "PHYSICAL_OFFICE";
    public static final String PROJECT = "PROJECT";
    public static final String PROJECT_POLICY = "PROJECT_POLICY";
    public static final String MEMBER_CLASSIFICATION = "MEMBER_CLASSIFICATION";
    public static final String SAVINGS_PRODUCT = "SAVINGS_PRODUCT";
    public static final String SAVINGS_PRODUCT_POLICY = "SAVINGS_PRODUCT_POLICY";
    public static final String RELATIONSHIPS = "RELATIONSHIPS";
    public static final String COUNTRY = "COUNTRY";
    public static final String GROUP = "GROUP";
    public static final String EMPLOYEE = "EMPLOYEE";

    private MemberSourceDataRequest() {
    }

    public static Map<String, String> getCreateMap(CreateMemberCommand command) {
        Map<String, String> request = new LinkedHashMap<>();
        request.put(PHYSICAL_OFFICE, command.getBranchInfoId());
        request.put(PROJECT, command.getProjectInfoId());
        request.put(PROJECT_POLICY, command.getProjectInfoId());
        request.put(MEMBER_CLASSIFICATION, command.getMemberClassificationId());
        request.put(SAVINGS_PRODUCT, command.getSavingsProductId());
        request.put(SAVINGS_PRODUCT_POLICY, command.getSavingsProductId());
        request.put(RELATIONSHIPS, "ALL");
        request.put(COUNTRY, "BD");
        if (command.getGroupInfoId() != null && !command.getGroupInfoId().isBlank()) {
            request.put(GROUP, command.getGroupInfoId());
        }
        if (command.getAssignedPoId() != null && !command.getAssignedPoId().isBlank()) {
            request.put(EMPLOYEE, command.getAssignedPoId());
        }
        return request;
    }
}
