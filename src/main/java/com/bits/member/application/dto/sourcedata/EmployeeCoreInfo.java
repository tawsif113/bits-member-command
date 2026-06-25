package com.bits.member.application.dto.sourcedata;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCoreInfo {
    private String id;
    private Long homeCountryId;
    private String pinNo;
    private LocalDate joiningDate;
    private String salutationId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String nickName;
    private Long genderId;
    private LocalDate employeeDob;
    private Long domainStatusId;
    private String employeeStatusId;
    private LocalDate provisionEndDate;
    private String curJobStatusId;
    private Boolean isOnDeputation;
    private LocalDate deputationEndDate;
    private LocalDate curJobStartDate;
    private String approvalStatusId;
    private LocalDate approvalDate;
    private Boolean isIssuedLetter;
    private Boolean isIssuedRetireLetter;
    private Long employeeLevelId;
    private Long eDesignationId;
    private Long fDesignationId;
    private String programTypeId;
    private String coreProjectId;
    private String coreProgramId;
    private String departmentId;
    private String supervisorId;
    private Long noticePeriod;
    private Long workingHour;
    private Long workingDayInWeek;
    private Long nomineeForm;
    private Boolean isExpatriate;
    private String emailAddress;
    private Long unitId;
    private String rollNo;
    private String recruitReqNo;
    private Long previousEmpCoreInfoId;
    private String employeeName;
    private String nationalIdNo;
    private String smartNIDNo;
    private Long payGroupId;
    private String currencyName;
    private Boolean isIncrement;
    private Boolean isLeave;
    private Boolean isAttendance;
    private LocalDate joiningDateASPA;
    private Long religionId;
    private Boolean isReceive;
    private Boolean isPostRefRequired;
    private Boolean isPostRefCompleted;
    private Boolean isBackCheckCompleted;
    private Long organogram;
    private Long position;
    private String refErecruitId;
    private Long tinTypeId;
    private String tinNumber;
    private Long createdBy;
    private Long updatedBy;
    private LocalDateTime dateCreated;
    private LocalDateTime lastUpdated;
    private String name;
    private String employeeCode;
    private String branchInfoId;
    private String projectInfoId;
    private Boolean active;

    public String id() {
        return id;
    }

    public Long homeCountryId() {
        return homeCountryId;
    }
    public String pinNo() {
        return pinNo;
    }
    public LocalDate joiningDate() {
        return joiningDate;
    }
    public String salutationId() {
        return salutationId;
    }
    public String firstName() {
        return firstName;
    }
    public String middleName() {
        return middleName;
    }
    public String lastName() {
        return lastName;
    }
    public String nickName() {
        return nickName;
    }
    public Long genderId() {
        return genderId;
    }
    public LocalDate employeeDob() {
        return employeeDob;
    }
    public Long domainStatusId() {
        return domainStatusId;
    }
    public String employeeStatusId() {
        return employeeStatusId;
    }
    public LocalDate provisionEndDate() {
        return provisionEndDate;
    }
    public String curJobStatusId() {
        return curJobStatusId;
    }
    public Boolean isOnDeputation() {
        return isOnDeputation;
    }
    public LocalDate deputationEndDate() {
        return deputationEndDate;
    }
    public LocalDate curJobStartDate() {
        return curJobStartDate;
    }
    public String approvalStatusId() {
        return approvalStatusId;
    }
    public LocalDate approvalDate() {
        return approvalDate;
    }
    public Boolean isIssuedLetter() {
        return isIssuedLetter;
    }
    public Boolean isIssuedRetireLetter() {
        return isIssuedRetireLetter;
    }
    public Long employeeLevelId() {
        return employeeLevelId;
    }
    public Long eDesignationId() {
        return eDesignationId;
    }
    public Long fDesignationId() {
        return fDesignationId;
    }
    public String programTypeId() {
        return programTypeId;
    }
    public String coreProjectId() {
        return coreProjectId;
    }
    public String coreProgramId() {
        return coreProgramId;
    }
    public String departmentId() {
        return departmentId;
    }
    public String supervisorId() {
        return supervisorId;
    }
    public Long noticePeriod() {
        return noticePeriod;
    }
    public Long workingHour() {
        return workingHour;
    }
    public Long workingDayInWeek() {
        return workingDayInWeek;
    }
    public Long nomineeForm() {
        return nomineeForm;
    }
    public Boolean isExpatriate() {
        return isExpatriate;
    }
    public String emailAddress() {
        return emailAddress;
    }
    public Long unitId() {
        return unitId;
    }
    public String rollNo() {
        return rollNo;
    }
    public String recruitReqNo() {
        return recruitReqNo;
    }
    public Long previousEmpCoreInfoId() {
        return previousEmpCoreInfoId;
    }
    public String employeeName() {
        return employeeName;
    }
    public String nationalIdNo() {
        return nationalIdNo;
    }
    public String smartNIDNo() {
        return smartNIDNo;
    }
    public Long payGroupId() {
        return payGroupId;
    }
    public String currencyName() {
        return currencyName;
    }
    public Boolean isIncrement() {
        return isIncrement;
    }
    public Boolean isLeave() {
        return isLeave;
    }
    public Boolean isAttendance() {
        return isAttendance;
    }
    public LocalDate joiningDateASPA() {
        return joiningDateASPA;
    }
    public Long religionId() {
        return religionId;
    }
    public Boolean isReceive() {
        return isReceive;
    }
    public Boolean isPostRefRequired() {
        return isPostRefRequired;
    }
    public Boolean isPostRefCompleted() {
        return isPostRefCompleted;
    }
    public Boolean isBackCheckCompleted() {
        return isBackCheckCompleted;
    }
    public Long organogram() {
        return organogram;
    }
    public Long position() {
        return position;
    }
    public String refErecruitId() {
        return refErecruitId;
    }
    public Long tinTypeId() {
        return tinTypeId;
    }
    public String tinNumber() {
        return tinNumber;
    }
    public Long createdBy() {
        return createdBy;
    }
    public Long updatedBy() {
        return updatedBy;
    }
    public LocalDateTime dateCreated() {
        return dateCreated;
    }
    public LocalDateTime lastUpdated() {
        return lastUpdated;
    }
    public String name() {
        return name;
    }
    public String employeeCode() {
        return employeeCode;
    }
    public String branchInfoId() {
        return branchInfoId;
    }
    public String projectInfoId() {
        return projectInfoId;
    }
    public Boolean active() {
        return active;
    }

}
