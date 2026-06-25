# Member — DDD/CQRS/Event-Sourcing Business Requirements Specification (EARS) — Command Side

> **Format:** Easy Approach to Requirements Syntax (EARS) — DDD/CQRS/ES Edition
> **Source:** Converted from multi-file layered-architecture EARS catalog `source/Member-Domain-EARS-Spec.md`
> **Architecture:** bits.ddd command-side service
> **Aggregate:** `Member` — MongoDB document, `members` collection
> **Output path:** `output/member/Member-Command-DDD-EARS.md`
> **Traceability:** Every DDD-REQ cites the source EARS section it was derived from, in the format: 📎 Source: {SourceFileName} § "{Section / Sub-section heading}"
> **Companion file:** `output/member/Member-Query-DDD-EARS.md` — query side (CQRS split)

---

## Planning Phase Gate Decisions

> This section records every architectural decision confirmed by the product owner / domain expert during the interactive planning phase (Phases 0–5 of the SKILL.md process). Each gate decision is binding: the DDD-REQ requirements below must implement exactly the choice made here. No gate may be re-opened without explicit sign-off.
>
> **Source:** Gate Decision Ledger maintained by the agent throughout Phases 0–5.

### Gate 0a — Source File Approach
**Phase:** 0 — Source File Confirmation
**Question asked:** "Is your EARS source a single file or multiple files?"
**Decision:** ✅ Multiple files — catalog-driven (Branch B). Catalog: `source/Member-Domain-EARS-Spec.md`
**Architectural effect:** All DDD-REQ source citations route to the originating EARS file per the catalog's `Originating EARS File` column. Deduplication registry applied to entities defined in both files.

---

### Gate 0b — Catalog Confirmation
**Phase:** 0 — Source File Confirmation
**Question asked:** "Catalog parsed: 19 actions across 2 EARS files. Does this look correct?"
**Decision:** ✅ Confirmed — 19 actions, 2 EARS files (`MemberAdmissionApproval-EARS-Specification.md`, `MemberManagement-EARS-Specification-Resolved.md`), 5 modules (Member Admission Approval, Member Activation, Member Management, Member Project-Officer Reassignment, Member Validation Rules).
**Architectural effect:** Governs which EARS file is cited for each DDD-REQ. Deduplication registry built during reading.

---

### Gate 1a — Domain Name, Aggregate Root, Domain Slug
**Phase:** 1 — Domain & Boundary Discovery
**Question asked:** "Please confirm or override the Domain Name and Aggregate Root name."
**Decision:** ✅ Domain Name = `Member`, Aggregate Root = `Member`, Domain Slug = `member`
**Architectural effect:** All output files under `output/member/`. Aggregate class: `Member extends AggregateRoot<String>`. MongoDB collection: `members`. All commands named `{Verb}MemberCommand`.

---

### Gate 1b — Aggregate Root Behaviors
**Phase:** 1 — Domain & Boundary Discovery
**Question asked:** "Please confirm the Aggregate Root behaviors."
**Decision:** ✅ 13 command-side behaviors confirmed: `CreateMember`, `ApproveMemberAdmission`, `UpdateMember`, `ApproveMemberUpdate`, `DeleteMember`, `ActivateMember`, `SaveMemberFamily`, `UpdateMemberFamily`, `UpdateMemberPhoto`, `UpdateMemberSignature`, `UpdateMembershipDocument`, `UpdateMemberAsset`, `ReassignProjectOfficer`. 4 query-side operations: `GetMemberById`, `EditMember`, `ListMembers`, `ValidateMember`.
**Architectural effect:** 13 Command + 13 CommandHandler classes generated. 4 Query operations go to the Query file.

---

### Gate 2a — Entity Roles
**Phase:** 2 — Structural Schema Definition
**Question asked:** "Please confirm or override the suggested DDD role for each entity."
**Decision:** ✅ `Member` = AGGREGATE_ROOT; `PersonalInfo`, `ContactInfo`, `MemberAddress`, `NomineeInfo`, `GuardianInfo`, `GuarantorInfo`, `PassbookInfo`, `FamilyInfo`, `OtherOrganizationLoan`, `HouseholdAsset` = ENTITY (embedded); `MembershipStatusChangeHistory`, `MembershipDocument` = VALUE_OBJECT; 14 external entities = SOURCE_DATA. Deduplication conflict on `MemberAddress` (countryId, zipCode added by MemberManagement EARS) → union of fields, flagged in Open Questions.
**Architectural effect:** Governs which entities become embedded sub-documents in `Member` vs. which become snapshot collections in Infrastructure Layer.

---

### Gate 2b — Sourcing Mechanism
**Phase:** 2 — Structural Schema Definition
**Question asked:** "How will source data be collected? Options: [1] EVENT or [2] HTTP."
**Decision:** ✅ EVENT — source data captured locally via RabbitMQ listener events from owning domains.
**Architectural effect:** Infrastructure Layer includes 14 `{Entity}SnapshotDocument` collections. Application Layer includes `MemberSourceDataFactory` with per-entity snapshot repositories. No HTTP client stubs generated.

---

### Gate 2c — Event Listener Timing
**Phase:** 2 — Structural Schema Definition
**Question asked:** "Do you want to implement the listeners to source data through events now?"
**Decision:** ✅ Yes — implement event listeners in full detail now.
**Architectural effect:** DDD-REQ-044 specifies a full `@RabbitListener` pipeline for each SOURCE_DATA entity: event DTO schema, handler implementation pseudocode, dedicated Spring Data MongoDB snapshot repository.

---

### Gate 3a — Request Entry Points
**Phase:** 3 — Action-by-Action Breakdown
**Question asked:** "How does the request for each behavior arrive? REST API or Event Listener?"
**Decision:** ✅ All 13 behaviors arrive via REST API. Each handler class exposes a `@RestController` endpoint via `MemberCommandController extending BaseApiController`.
**Architectural effect:** Single `MemberCommandController` with 13 endpoints. No `@RabbitListener` command entry points.

---

### Gate 3b — Specification Aggregation Logic (Create & ApproveMemberAdmission)
**Phase:** 3 — Action-by-Action Breakdown
**Question asked:** "What is the logical composition strategy for these specifications?"
**Decision:** ✅ Strict AND chain — all 8 specification categories run; all violations accumulated into a single error map before throwing (non-short-circuiting).
**Architectural effect:** Both `create()` and `approveMemberAdmission()` run the full 8-spec strict AND chain. Each is a distinct AR method. Shared validation logic is extracted to a private `performFullDomainValidation(context)` helper on the AR; each method calls the helper independently and then emits its own semantically distinct event (`MemberCreatedEvent` vs `MemberAdmissionApprovedEvent`). Handlers (`CreateMemberCommandHandler`, `ApproveMemberAdmissionCommandHandler`) orchestrate only — they never call `addEvent(...)` directly.

---

### Gate 3c — Specification Aggregation Logic (Update & ApproveMemberUpdate)
**Phase:** 3 — Action-by-Action Breakdown
**Question asked:** "What is the logical composition strategy for the update behaviors?"
**Decision:** ✅ Conditional AND chain — specs run with null-guarded inputs (skip sub-rules for unchanged fields); fail-fast on first error returned by any spec.
**Architectural effect:** Both `update()` and `approveMemberUpdate()` run the full 8-spec conditional AND chain. Each is a distinct AR method. Shared validation logic is extracted to a private `performConditionalDomainValidation(context)` helper on the AR; each method calls the helper independently and then emits its own semantically distinct event (`MemberUpdatedEvent` vs `MemberUpdateApprovedEvent`). Handlers (`UpdateMemberCommandHandler`, `ApproveMemberUpdateCommandHandler`) orchestrate only — they never call `addEvent(...)` directly.

---

### Gate 3d — Behavior-to-Specification Applicability Matrix
**Phase:** 3 — Action-by-Action Breakdown
**Question asked:** "Is this behavior-to-specification matrix correct?"
**Decision:** ✅ Matrix confirmed. Create/ApproveMemberAdmission: all 8 specs ✅. Update/ApproveMemberUpdate: all 8 specs ✅ (conditional). Delete: HANDLER_GUARD only. Activate: HANDLER_GUARD + business day only. SaveMemberFamily: `NomineeAndGuarantorSpecification` only. Others (7): field-level validation, no domain spec chain.
**Architectural effect:** Governs `NNa Applicability Matrix` (DDD-REQ-023a) and which behaviors construct a `MemberValidationContext`.

---

### Gate 4 — Output File Format
**Phase:** 4 — Query & Read Model Definition
**Question asked:** "Single unified file or split into command + query files?"
**Decision:** ✅ Split — `output/member/Member-Command-DDD-EARS.md` (this file) + `output/member/Member-Query-DDD-EARS.md`.
**Architectural effect:** Query / Read Model section goes entirely to the Query file. This file covers command side only.

---

### Gate 5a — Saga Management
**Phase:** 5 — Saga, Integration & Cross-Cutting
**Question asked:** "Does this domain require Saga management or Process Managers for distributed transactions?"
**Decision:** ✅ No Saga — this domain owns its full transaction boundary. Cross-domain side effects (savings account creation, fee transactions, downstream queue publish, voucher processing) are async fire-and-forget.
**Architectural effect:** No Process Manager or choreography listener generated. Downstream publications happen via `MessageProcessor.publish(aggregate.getEvents())` after successful persistence.

---

### Gate 5b — Security, Idempotency & Audit Logging
**Phase:** 5 — Saga, Integration & Cross-Cutting
**Question asked:** "Are there any additional idempotency keys, security token policies, or audit-logging structures required?"
**Decision:** ✅ No additional requirements. Use EARS-specified: session authentication, database-driven feature-action ACL, optimistic-locking version, concurrency locks (identity lock + branch-project-group lock for Create/ApproveMemberAdmission), audit fields (createdBy, updatedBy, dateCreated, lastUpdated) on all records.
**Architectural effect:** Cross-cutting DDD-REQs (DDD-REQ-119 through DDD-REQ-122) reproduce the EARS-specified auth, lock, audit, and async-publish requirements verbatim.

---

## Document Conventions

| Marker | Meaning |
|--------|---------|
| `[INFERRED]` | Required by bits.ddd pattern; no explicit requirement in source EARS |
| `[UNCHANGED]` | Copied verbatim from the original EARS (cross-cutting concern) |
| `[PSEUDOCODE]` | Java-adjacent pseudocode block — describes behaviour without full implementation syntax |
| `[READ-MODEL]` | CQRS query-side requirement — in Query file only |
| `[ASYNC]` | Asynchronous side effect |
| `[OUT-OF-SCOPE]` | Identified in source EARS but excluded from this DDD specification |
| `[CONFLICT]` | Definition conflict between EARS files — union of fields used; see Open Questions |

**Source citation format (all DDD-REQs use this):**
```
📎 Source: {SourceFileName} § "{Section / Sub-section heading}"
```
- `MemberAdmissionApproval-EARS-Specification.md` = admission approval EARS
- `MemberManagement-EARS-Specification-Resolved.md` = member management EARS

---

## Behavior Cross-Reference Index

> This index cross-references every command-side behavior to its related DDD-REQ numbers. Use it to find all specifications for a given behavior without scanning the full document.

### Command-Side Behaviors

| Behavior | Entry Point | Command | Handler | AR Method | Event Emitted | DDD-REQ Numbers |
|----------|------------|---------|---------|-----------|--------------|-----------------|
| CreateMember | POST `/api/members` | `CreateMemberCommand` | `CreateMemberCommandHandler` | `.create()` | `MemberCreatedEvent` | DDD-REQ-001, 014–023, 027, 040–043, 045, 059–061, 074, 087 |
| ApproveMemberAdmission | POST `/api/members/admission/approve` | `ApproveMemberAdmissionCommand` | `ApproveMemberAdmissionCommandHandler` | `.approveMemberAdmission()` | `MemberAdmissionApprovedEvent` | DDD-REQ-001, 014–023, 030, 040–043, 046, 062, 075, 087 |
| UpdateMember | PUT `/api/members/{id}` | `UpdateMemberCommand` | `UpdateMemberCommandHandler` | `.update()` | `MemberUpdatedEvent` | DDD-REQ-001, 014–023, 028, 040–043, 047, 059–060, 063, 076, 087 |
| ApproveMemberUpdate | PUT `/api/members/admission/update/{id}` | `ApproveMemberUpdateCommand` | `ApproveMemberUpdateCommandHandler` | `.approveMemberUpdate()` | `MemberUpdateApprovedEvent` | DDD-REQ-001, 014–023, 031, 040–043, 048, 064, 077, 087 |
| DeleteMember | DELETE `/api/members/{id}` | `DeleteMemberCommand` | `DeleteMemberCommandHandler` | `.delete()` | `MemberDeletedEvent` | DDD-REQ-001, 029, 049, 059–060, 065, 078, 087 |
| ActivateMember | PUT `/api/members/activate` | `ActivateMemberCommand` | `ActivateMemberCommandHandler` | `.activate()` | `MemberActivatedEvent` | DDD-REQ-001, 032, 050, 060, 066, 079, 087 |
| SaveMemberFamily | POST `/api/members/{id}/family` | `SaveMemberFamilyCommand` | `SaveMemberFamilyCommandHandler` | `.saveFamily()` | `MemberFamilySavedEvent` | DDD-REQ-001, 021, 033, 051, 059–060, 067, 080, 087 |
| UpdateMemberFamily | PUT `/api/members/{id}/family` | `UpdateMemberFamilyCommand` | `UpdateMemberFamilyCommandHandler` | `.updateFamily()` | `MemberFamilyUpdatedEvent` | DDD-REQ-001, 034, 052, 059–060, 068, 081, 087 |
| UpdateMemberPhoto | PUT `/api/members/{id}/photo` | `UpdateMemberPhotoCommand` | `UpdateMemberPhotoCommandHandler` | `.updatePhoto()` | `MemberPhotoUpdatedEvent` | DDD-REQ-001, 035, 053, 059–060, 069, 082, 087 |
| UpdateMemberSignature | PUT `/api/members/{id}/signature` | `UpdateMemberSignatureCommand` | `UpdateMemberSignatureCommandHandler` | `.updateSignature()` | `MemberSignatureUpdatedEvent` | DDD-REQ-001, 036, 054, 059–060, 070, 083, 087 |
| UpdateMembershipDocument | PUT `/api/members/{id}/documents` | `UpdateMembershipDocumentCommand` | `UpdateMembershipDocumentCommandHandler` | `.updateMembershipDocument()` | `MembershipDocumentUpdatedEvent` | DDD-REQ-001, 037, 055, 059–060, 071, 084, 087 |
| UpdateMemberAsset | PUT `/api/members/{id}/assets` | `UpdateMemberAssetCommand` | `UpdateMemberAssetCommandHandler` | `.updateAssets()` | `MemberAssetUpdatedEvent` | DDD-REQ-001, 038, 056, 059–060, 072, 085, 087 |
| ReassignProjectOfficer | PUT `/api/members/reassign-po` | `ReassignProjectOfficerCommand` | `ReassignProjectOfficerCommandHandler` | `.reassignProjectOfficer()` | `MemberProjectOfficerReassignedEvent` | DDD-REQ-001, 039, 057, 059–060, 073, 086, 087 |

### Query-Side Operations

Defined in companion file `output/member/Member-Query-DDD-EARS.md`.

| Operation | Entry Point | Query | Handler | Response DTO |
|-----------|------------|-------|---------|-------------|
| GetMemberById | GET `/api/members/{key}/{id}` | `GetMemberByIdQuery` | `GetMemberByIdQueryHandler` | `MemberDetailResponse` |
| EditMember | GET `/api/members/{id}/edit` | `EditMemberQuery` | `EditMemberQueryHandler` | `MemberEditResponse` |
| ListMembers | GET `/api/members/{key}` | `SearchMembersQuery` | `SearchMembersQueryHandler` | `Page<MemberListItem>` |
| ValidateMember | GET `/api/members/validate` | `ValidateMemberQuery` | `ValidateMemberQueryHandler` | `MemberValidationResponse` |

---

## Domain Layer

### Command-Side Inventory Matrix

| # | Item Type | Count | Items |
|---|-----------|-------|-------|
| 1 | Aggregate Roots | 1 | `Member` |
| 2 | Embedded Entities | 10 | `PersonalInfo`, `ContactInfo`, `MemberAddress`, `NomineeInfo`, `GuardianInfo`, `GuarantorInfo`, `PassbookInfo`, `FamilyInfo`, `OtherOrganizationLoan`, `HouseholdAsset` |
| 3 | Value Objects | 2 | `MembershipStatusChangeHistory`, `MembershipDocument` |
| 4 | Source Data Elements | 14 | `PhysicalOfficeInfo`, `ProjectInfo`, `ProjectPolicyInfo`, `GroupInfo`, `EmployeeCoreInfo`, `MemberClassification`, `SavingsProduct`, `SavingsProductPolicy`, `Relationship`, `Country`, `MemberStatus`, `Occupation`, `SavingsAccount`, `Thana` |
| 5 | Validation Specification Categories | 8 | `FieldPresenceAndFormatSpecification`, `IdentityDocumentSpecification`, `BusinessDayAndBranchSpecification`, `MemberCategoryAndGroupPolicySpecification`, `SavingsProductSpecification`, `DeduplicationSpecification`, `NomineeAndGuarantorSpecification`, `PersonalDataConsistencySpecification` |
| 6 | Commands | 13 | `CreateMemberCommand`, `ApproveMemberAdmissionCommand`, `UpdateMemberCommand`, `ApproveMemberUpdateCommand`, `DeleteMemberCommand`, `ActivateMemberCommand`, `SaveMemberFamilyCommand`, `UpdateMemberFamilyCommand`, `UpdateMemberPhotoCommand`, `UpdateMemberSignatureCommand`, `UpdateMembershipDocumentCommand`, `UpdateMemberAssetCommand`, `ReassignProjectOfficerCommand` |
| 7 | Command Handlers | 13 | `CreateMemberCommandHandler`, `ApproveMemberAdmissionCommandHandler`, `UpdateMemberCommandHandler`, `ApproveMemberUpdateCommandHandler`, `DeleteMemberCommandHandler`, `ActivateMemberCommandHandler`, `SaveMemberFamilyCommandHandler`, `UpdateMemberFamilyCommandHandler`, `UpdateMemberPhotoCommandHandler`, `UpdateMemberSignatureCommandHandler`, `UpdateMembershipDocumentCommandHandler`, `UpdateMemberAssetCommandHandler`, `ReassignProjectOfficerCommandHandler` |
| 8 | Domain Events (success) | 13 | `MemberCreatedEvent`, `MemberUpdatedEvent`, `MemberDeletedEvent`, `MemberActivatedEvent`, `MemberFamilySavedEvent`, `MemberFamilyUpdatedEvent`, `MemberPhotoUpdatedEvent`, `MemberSignatureUpdatedEvent`, `MembershipDocumentUpdatedEvent`, `MemberAssetUpdatedEvent`, `MemberProjectOfficerReassignedEvent`, `MemberAdmissionApprovedEvent`, `MemberUpdateApprovedEvent` |
| 9 | Domain Events (failure) | 1 | `MemberFailedEvent` |

Every item declared in this matrix has its complete schema in the DDD-REQs below.

---

### DDD-REQ-001a — AR Behavior Map: Events Originated & Specifications Used

> **Rule:** Every `addEvent(...)` call MUST live inside the AR behavior method listed in the "AR Method" column.
> Handlers (`CreateMemberCommandHandler`, `ApproveMemberAdmissionCommandHandler`, etc.) MUST NEVER call `addEvent(...)` — they only call `messagingProcessor.publish(member.getEvents())` after persistence.
> Shared validation logic between `create()`/`approveMemberAdmission()` and `update()`/`approveMemberUpdate()` is extracted to **private helper methods** inside the AR.

| # | Behavior | AR Method | Event Originated (inside method) | Specifications Used | Source EARS |
|---|----------|-----------|----------------------------------|---------------------|-------------|
| 1 | CreateMember | `.create(creationData)` | `MemberCreatedEvent` | All 8 specs — strict AND chain (via `performFullDomainValidation`) | MemberManagement § "Member Registration" |
| 2 | ApproveMemberAdmission | `.approveMemberAdmission(admissionData)` | `MemberAdmissionApprovedEvent` | All 8 specs — strict AND chain (via `performFullDomainValidation`) | MemberAdmissionApproval § "New-Admission Approval Flow" |
| 3 | UpdateMember | `.update(updateData)` | `MemberUpdatedEvent` | All 8 specs — conditional AND chain (via `performConditionalDomainValidation`) | MemberManagement § "Member Profile Update" |
| 4 | ApproveMemberUpdate | `.approveMemberUpdate(updateApprovalData)` | `MemberUpdateApprovedEvent` | All 8 specs — conditional AND chain (via `performConditionalDomainValidation`) | MemberAdmissionApproval § "Member-Update Approval Flow" |
| 5 | DeleteMember | `.delete(deletionData)` | `MemberDeletedEvent` | None — HANDLER_GUARD only (status + loan + savings guards); no `MemberValidationContext` constructed | MemberManagement § "Member Deletion" |
| 6 | ActivateMember | `.activate(activationData)` | `MemberActivatedEvent` | `BusinessDayAndBranchSpecification` only (business day check) | MemberManagement § "Member Activation" |
| 7 | SaveMemberFamily | `.saveFamily(familyData)` | `MemberFamilySavedEvent` | `NomineeAndGuarantorSpecification` only (family composition rules) | MemberManagement § "Member Family" |
| 8 | UpdateMemberFamily | `.updateFamily(familyData)` | `MemberFamilyUpdatedEvent` | None — field-level validation only; no domain spec chain | MemberManagement § "Member Family" |
| 9 | UpdateMemberPhoto | `.updatePhoto(photoData)` | `MemberPhotoUpdatedEvent` | None — field-level validation only | MemberManagement § "Member Photo" |
| 10 | UpdateMemberSignature | `.updateSignature(signatureData)` | `MemberSignatureUpdatedEvent` | None — field-level validation only | MemberManagement § "Member Signature" |
| 11 | UpdateMembershipDocument | `.updateMembershipDocument(docData)` | `MembershipDocumentUpdatedEvent` | None — field-level validation only | MemberManagement § "Membership Document" |
| 12 | UpdateMemberAsset | `.updateAssets(assetData)` | `MemberAssetUpdatedEvent` | None — field-level validation only | MemberManagement § "Member Asset" |
| 13 | ReassignProjectOfficer | `.reassignProjectOfficer(reassignData)` | `MemberProjectOfficerReassignedEvent` | None — HANDLER_GUARD only (active status check) | MemberManagement § "Reassign Project Officer" |

> 📎 [INFERRED] — required by bits.ddd domain event origination pattern
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management"
> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Module: Member Admission Approval"

---

### DDD-REQ-001 — Aggregate Root: Member

The Member domain system shall implement `Member` as the aggregate root, extending `AggregateRoot<String>` from `com.bits.ddd.domain.aggregate`. The aggregate shall be annotated with `@Document(collection = "members")`. Its identity shall be a UUID string generated at creation time.

The aggregate shall own the following fields:

| Field | Type | Description | Constraints |
|-------|------|-------------|-------------|
| `id` | `String` | UUID, generated at creation. MongoDB `_id`. | required |
| `countryId` | `String` | Reference to Country snapshot | required |
| `branchInfoId` | `String` | Reference to PhysicalOfficeInfo snapshot (branch office) | required |
| `assignedPoId` | `String` | Reference to EmployeeCoreInfo snapshot (field officer) | nullable |
| `projectInfoId` | `String` | Reference to ProjectInfo snapshot | required |
| `groupInfoId` | `String` | Reference to GroupInfo snapshot (VO); null for direct-association projects | nullable |
| `memberNo` | `String` | Auto-generated member number | required |
| `referenceNo` | `String` | External reference number | nullable, max 15 |
| `groupRefNo` | `String` | Group reference number | nullable, max 15 |
| `memberName` | `String` | Full member name (concatenated from fName, mName, lName) | required, max 50, must not begin with digit |
| `fName` | `String` | First name component | nullable |
| `mName` | `String` | Middle name component | nullable |
| `lName` | `String` | Last name component | nullable |
| `applicationDate` | `LocalDate` | Membership application date | required |
| `surveyReportNo` | `String` | Survey report number | nullable, max 15 |
| `membershipDate` | `LocalDate` | Membership effective date; set to branch business date at registration | required |
| `lastPoAssignedDate` | `LocalDate` | Date the current project officer was assigned | nullable |
| `memberStatusId` | `String` | Reference to MemberStatus (Active = "1") | required |
| `expiredDate` | `LocalDate` | Membership expiry date | nullable |
| `closingDate` | `LocalDate` | Member closing date | nullable |
| `domainStatus` | `DomainStatus` | bits.ddd lifecycle status; set to `CREATED` at registration | required |
| `memberDomainStatus` | `Integer` | Local active/inactive flag: 1 = active, 2 = inactive | required, default 1 |
| `isTransferredMember` | `Boolean` | Whether the member was transferred in | default false |
| `closeReasonId` | `String` | Reference to CloseReason | nullable |
| `transferTransactionRefNo` | `String` | Transfer transaction reference | nullable |
| `loanCycleNo` | `Integer` | Loan cycle counter | default 0 |
| `memberCustomField` | `String` | Project-specific custom classification code | nullable, max 4, pattern "P"\|"M" + 3 digits |
| `memberClassificationId` | `String` | Reference to MemberClassification (category) | required |
| `referredBy` | `String` | Referrer name | nullable |
| `passbookNo` | `String` | Current passbook number | nullable |
| `tinNumber` | `String` | Taxpayer identification number | nullable, max 12, alphanumeric |
| `academicQualificationId` | `String` | Academic qualification reference | nullable |
| `bankId` | `String` | Bank reference | nullable |
| `bankBranchId` | `String` | Bank branch reference | nullable |
| `bankAccountNumber` | `String` | Bank account number | nullable |
| `routingNumber` | `String` | Bank routing number | nullable |
| `incidentStatus` | `Integer` | Incident flag: 1 = disability, 0 = death | nullable |
| `bufferId` | `String` | Originating DCS buffer record identifier | nullable |
| `apiDataSourceId` | `Integer` | Channel origin: 1 = SmartPo, 2 = DCS | nullable |
| `uuidNo` | `Long` | Universal identifier derived from identity documents | nullable |
| `personalInfo` | `PersonalInfo` | Embedded personal and identity details | required |
| `contactInfo` | `ContactInfo` | Embedded contact information | required |
| `familyInfo` | `FamilyInfo` | Embedded family composition | nullable |
| `nominees` | `List<NomineeInfo>` | Embedded nominee list (cascade all, delete-orphan) | nullable |
| `passbooks` | `List<PassbookInfo>` | Embedded passbook list (cascade all, delete-orphan) | nullable |
| `guardianInfo` | `GuardianInfo` | Embedded guardian for minor nominees | nullable |
| `guarantorInfo` | `GuarantorInfo` | Embedded loan guarantor | nullable |
| `statusHistory` | `List<MembershipStatusChangeHistory>` | Embedded status-change audit entries | nullable |
| `assets` | `List<HouseholdAsset>` | Embedded household asset list | nullable |
| `signatureReference` | `String` | File reference to member's signature image | nullable |
| `membershipDocuments` | `MembershipDocument` | Embedded membership document file references | nullable |
| `createdBy` | `String` | Creating operator identifier | required |
| `updatedBy` | `String` | Last-updating operator identifier | required |
| `dateCreated` | `LocalDateTime` | Creation timestamp | required |
| `lastUpdated` | `LocalDateTime` | Last-update timestamp | required |
| `version` | `Long` | Optimistic-locking version | required |

**Index candidates:**

| Index name | Fields | Type | Query / guard this supports |
|-----------|--------|------|-----------------------------|
| `_id` | `_id` | Default (auto) | Aggregate load by ID |
| `idx_member_no` | `member_no ASC` | Unique | Member number uniqueness guard |
| `idx_branch_project_status` | `branch_info_id ASC, project_info_id ASC, member_domain_status ASC` | Compound | Branch-project member listing |
| `idx_project_group_status` | `project_info_id ASC, group_info_id ASC, member_domain_status ASC` | Compound | Group-based listing |
| `idx_buffer_id` | `buffer_id ASC` | Sparse unique | DCS buffer record lookup |
| `idx_uuid_no` | `uuid_no ASC` | Sparse | Identity-uniqueness guard |
| `idx_sp_national_id` | `personal_info.sp_national_id ASC` | Sparse | Spouse NID uniqueness guard |
| `idx_assigned_po` | `assigned_po_id ASC, branch_info_id ASC` | Compound | PO reassignment query |

---

**Behaviour — create(creationData):** [PSEUDOCODE]

> Source rule: "When an operator submits a new member, the Member Management system shall register the member at the operator's branch office…" (MemberManagement § "Member Registration") and "The Member Admission Approval system shall, on a new-admission request, accumulate all field-presence and format failures and report them together as a single combined failure message rather than stopping at the first." (MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request")
> → ALL 8 category specifications run. Structural/field validation at presentation layer (`@Valid`); the spec chain below is the domain-invariant layer. All violations accumulated before throwing.

```pseudocode
create(creationData):
  // ── Pre-population guards ────────────────────────────────────────────────
  // Concurrency locks (identity lock + branch-project-group lock) acquired by handler BEFORE calling create()

  // ── Field population ─────────────────────────────────────────────────────
  id                 = UUID.generate()
  memberNo           = creationData.memberNo        // auto-generated by handler
  memberName         = buildFullName(creationData.fName, creationData.mName, creationData.lName)
  fName              = creationData.fName
  mName              = creationData.mName
  lName              = creationData.lName
  applicationDate    = creationData.applicationDate
  membershipDate     = creationData.businessDate    // always set to branch business date
  memberStatusId     = MemberStatus.ACTIVE          // "1" = Active
  memberDomainStatus = 1                            // active
  domainStatus       = DomainStatus.CREATED
  isTransferredMember = false
  loanCycleNo        = 0
  countryId          = creationData.countryId
  projectInfoId      = creationData.projectInfoId
  memberClassificationId = creationData.memberClassificationId
  branchInfoId       = creationData.branchInfoId    // from group or operator office
  groupInfoId        = creationData.groupInfoId     // null for direct-association projects
  assignedPoId       = creationData.assignedPoId
  lastPoAssignedDate = creationData.businessDate
  passbookNo         = creationData.passbookNo      // null if no passbook supplied
  personalInfo       = creationData.personalInfo
  contactInfo        = creationData.contactInfo
  nominees           = creationData.nominees
  bufferId           = creationData.bufferId        // set when via DCS channel
  apiDataSourceId    = creationData.apiDataSourceId
  uuidNo             = deriveUuidNo(personalInfo)
  tinNumber          = creationData.tinNumber
  bankId             = creationData.bankId
  bankBranchId       = creationData.bankBranchId
  bankAccountNumber  = creationData.bankAccountNumber
  routingNumber      = creationData.routingNumber
  referredBy         = creationData.referredBy
  academicQualificationId = creationData.academicQualificationId
  memberCustomField  = creationData.memberCustomField
  createdBy          = creationData.operatorId
  updatedBy          = creationData.operatorId
  dateCreated        = now()
  lastUpdated        = now()

  // ── Validation context (all source-data entities loaded for CREATE) ───────
  context = new MemberValidationContext(
    physicalOfficeInfo    = creationData.physicalOfficeInfo,    // specs 3, 4
    projectInfo           = creationData.projectInfo,           // specs 1, 4
    projectPolicyInfo     = creationData.projectPolicyInfo,     // specs 4, 5
    groupInfo             = creationData.groupInfo,             // spec 4
    employeeCoreInfo      = creationData.employeeCoreInfo,      // spec 4
    memberClassification  = creationData.memberClassification,  // spec 4
    savingsProduct        = creationData.savingsProduct,        // spec 5
    savingsProductPolicy  = creationData.savingsProductPolicy,  // spec 5
    relationships         = creationData.relationships,         // spec 7
    businessDate          = creationData.businessDate,          // specs 1, 3
    deduplicationResult   = creationData.deduplicationResult,   // spec 6
    aggregate             = this)                               // all specs

  // ── Full 8-specification AND chain (CREATE) — all violations accumulated ─
  compositeSpec =
    new FieldPresenceAndFormatSpecification<>()               // required fields, format checks
        .and(new IdentityDocumentSpecification<>())            // NID/SmartCard/Passport structure per party
        .and(new BusinessDayAndBranchSpecification<>())        // branch type, business day open, app date
        .and(new MemberCategoryAndGroupPolicySpecification<>())// age, gender, group/officer assignment
        .and(new SavingsProductSpecification<>())              // product validity, target amount floor
        .and(new DeduplicationSpecification<>())               // cross-org identity uniqueness
        .and(new NomineeAndGuarantorSpecification<>())         // nominee count, relationships, guardian
        .and(new PersonalDataConsistencySpecification<>())     // member/spouse/guarantor doc cross-check

  errors = compositeSpec.validate(context)   // non-short-circuiting; accumulate all violations
  IF errors is not empty THEN
    THROW MemberValidationException(
      MemberFailedEvent.validationError(creationData.traceId, errors))
  END IF

  // ── Post-validation mutations ────────────────────────────────────────────
  // Distribute nominee shares equally (100% ÷ nominee count, 2 decimal places)
  IF nominees is not empty THEN
    equalShare = roundHalfUp(100.0 / nominees.size(), 2)
    FOR EACH nominee IN nominees DO nominee.sharePercent = equalShare END FOR
  END IF

  // Record initial status-change history (zero status → Active)
  statusHistory = [new MembershipStatusChangeHistory(
    oldMemberStatusId = 0,
    memberStatusId    = MemberStatus.ACTIVE,
    statusChangeDate  = creationData.businessDate,
    projectInfoId     = this.projectInfoId,
    groupInfoId       = this.groupInfoId,
    officeInfoId      = this.branchInfoId,
    domainStatus      = DomainStatus.CREATED)]

  addEvent(MemberEventMapper.toCreatedEvent(this))
  RETURN this
```

---

**Behaviour — update(updateData):** [PSEUDOCODE]

> Source rule: "When an operator submits changes to an existing member, the Member Management system shall update the member's basic, personal and contact information and addresses, recompute the member's full name…" (MemberManagement § "Member Profile Update") and "The Member Admission Approval system shall, on a member-update request, validate only the fields that are supplied, applying each rule below only when its field is present, and shall stop at the first failing rule." (MemberAdmissionApproval § "Required-Field, Format and Business Validation — Member-Update Request")
> → ALL 8 category specifications run with null-guarded context inputs. Status guard runs BEFORE the spec chain.

```pseudocode
update(updateData):
  // ── Status guard (runs BEFORE spec chain) ────────────────────────────────
  IF this.memberDomainStatus == 2 THEN   // inactive
    THROW MemberValidationException(
      MemberFailedEvent.validationError(updateData.traceId,
        Map.of(MessageKey.MEMBER.getKey(),
          LocalizedMessage(key = MessageKey.MEMBER_INACTIVE.getKey(),
            args = [this.memberNo]))))
  END IF

  // ── Field merge (non-null fields from updateData) ─────────────────────────
  IF updateData.memberName is not null THEN this.memberName = updateData.memberName END IF
  IF updateData.fName is not null      THEN this.fName = updateData.fName END IF
  IF updateData.mName is not null      THEN this.mName = updateData.mName END IF
  IF updateData.lName is not null      THEN this.lName = updateData.lName END IF
  // Recompute full name
  this.memberName = buildFullName(
    this.fName ?? updateData.fName,
    this.mName ?? updateData.mName,
    this.lName ?? updateData.lName)
  IF updateData.personalInfo is not null THEN this.personalInfo = this.personalInfo.partialUpdate(updateData.personalInfo) END IF
  IF updateData.contactInfo is not null  THEN this.contactInfo  = this.contactInfo.partialUpdate(updateData.contactInfo) END IF
  IF updateData.nominees is not null     THEN this.nominees = updateData.nominees END IF
  IF updateData.passbookNo is not null   THEN this.passbookNo = updateData.passbookNo END IF
  // ... merge remaining non-null fields: bankId, bankBranchId, bankAccountNumber, routingNumber,
  //     memberClassificationId, memberCustomField, tinNumber, academicQualificationId, referredBy
  this.uuidNo     = deriveUuidNo(this.personalInfo)
  this.updatedBy  = updateData.operatorId
  this.lastUpdated = now()

  // ── Validation context (conditional source-data on UPDATE) ───────────────
  // Only include a source entity when its corresponding command field is non-null
  context = new MemberValidationContext(
    physicalOfficeInfo    = updateData.physicalOfficeInfo,       // always re-fetched (branch required)
    projectInfo           = updateData.projectInfo,              // always present
    projectPolicyInfo     = updateData.projectPolicyInfo,        // always present
    groupInfo             = updateData.groupInfo,                // present when groupInfoId unchanged (via existing member)
    employeeCoreInfo      = updateData.employeeCoreInfo,         // present when assignedPoId updated
    memberClassification  = updateData.memberClassification,     // present when memberClassificationId updated
    savingsProduct        = updateData.savingsProduct,           // present when savingsProductId updated
    savingsProductPolicy  = updateData.savingsProductPolicy,     // present when savingsProductId updated
    relationships         = updateData.relationships,            // present when nominees/guarantor updated
    businessDate          = updateData.businessDate,
    deduplicationResult   = updateData.deduplicationResult,      // present when identity docs changed
    existingNominees      = this.nominees,                       // for 3-nominee cap check on update
    aggregate             = this)

  // ── Conditional AND chain (UPDATE) — fail-fast on first error ────────────
  compositeSpec =
    new FieldPresenceAndFormatSpecification<>()               // supplied-field format checks only
        .and(new IdentityDocumentSpecification<>())            // identity structure when supplied
        .and(new BusinessDayAndBranchSpecification<>())        // branch + business day
        .and(new MemberCategoryAndGroupPolicySpecification<>())// age range, gender when fields changed
        .and(new SavingsProductSpecification<>())              // savings product when changed
        .and(new DeduplicationSpecification<>())               // identity uniqueness when identity changed
        .and(new NomineeAndGuarantorSpecification<>())         // nominee/guarantor rules when supplied
        .and(new PersonalDataConsistencySpecification<>())     // doc cross-check when docs changed

  errors = compositeSpec.validate(context)   // fail-fast: return immediately on first spec error
  IF errors is not empty THEN
    THROW MemberValidationException(
      MemberFailedEvent.validationError(updateData.traceId, errors))
  END IF

  // ── Post-validation nominee share redistribution ─────────────────────────
  IF updateData.nominees is not null AND nominees is not empty THEN
    equalShare = roundHalfUp(100.0 / nominees.size(), 2)
    FOR EACH nominee IN nominees DO nominee.sharePercent = equalShare END FOR
  END IF

  this.status = DomainStatus.UPDATED
  addEvent(MemberEventMapper.toUpdatedEvent(this))
```

---

**Behaviour — approveMemberAdmission(admissionData):** [PSEUDOCODE]

> Source rule: "When the DCS member channel triggers admission approval, the Member Admission Approval system shall retrieve the buffered member record, validate all fields against the same domain invariants as direct registration, and — on success — persist the member and post back the provisioned member ID to DCS." (MemberAdmissionApproval § "New-Admission Approval Flow")
> → ALL 8 category specifications run via the same **private `performFullDomainValidation(context)`** helper as `create()`. This is a **separate AR method** from `create()` because it represents a distinct business outcome: an admission routed through the DCS buffer channel, emitting `MemberAdmissionApprovedEvent` (not `MemberCreatedEvent`) and carrying DCS-specific metadata (bufferId, passbookNo, admissionDate).

```pseudocode
approveMemberAdmission(admissionData):
  // ── Field population (same as create, from DCS buffer data) ────────────────
  id              = UUID.generate()
  memberNo        = admissionData.memberNo       // pre-generated by handler
  fName           = admissionData.fName
  mName           = admissionData.mName
  lName           = admissionData.lName
  memberName      = buildFullName(fName, mName, lName)
  personalInfo    = admissionData.personalInfo
  contactInfo     = admissionData.contactInfo
  nominees        = admissionData.nominees
  passbookNo      = admissionData.passbookNo     // DCS-specific: from request or DCS buffer
  bufferId        = admissionData.bufferId        // DCS buffer record ID
  admissionDate   = admissionData.admissionDate  // DCS-specific: date from DCS channel
  // ... populate all remaining fields from admissionData ...
  createdBy       = admissionData.operatorId
  dateCreated     = now()

  // ── Validation context (identical to create — all 14 source entities loaded) ─
  context = new MemberValidationContext(
    physicalOfficeInfo    = admissionData.physicalOfficeInfo,   // specs 3, 4
    projectInfo           = admissionData.projectInfo,          // specs 1, 4
    projectPolicyInfo     = admissionData.projectPolicyInfo,    // specs 4, 5, 7
    groupInfo             = admissionData.groupInfo,            // spec 4
    employeeCoreInfo      = admissionData.employeeCoreInfo,     // spec 1
    memberClassification  = admissionData.memberClassification, // specs 4, 5
    savingsProduct        = admissionData.savingsProduct,       // spec 5
    savingsProductPolicy  = admissionData.savingsProductPolicy, // spec 5
    relationships         = admissionData.relationships,        // spec 7
    businessDate          = admissionData.businessDate,         // spec 3
    deduplicationResult   = admissionData.deduplicationResult,  // spec 6
    country               = admissionData.country,              // spec 2
    aggregate             = this)

  // ── Full 8-spec strict AND chain ─────────────────────────────────────────────
  performFullDomainValidation(context, admissionData.traceId)
  // (private helper — same chain as create(); shared to avoid duplication)

  // ── DCS-specific post-validation fields ──────────────────────────────────────
  this.memberDomainStatus = 1  // active
  this.status = DomainStatus.CREATED
  addEvent(MemberEventMapper.toAdmissionApprovedEvent(this))
  RETURN this
```

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Module: Member Admission Approval — New-Admission Approval Flow"

---

**Behaviour — approveMemberUpdate(updateApprovalData):** [PSEUDOCODE]

> Source rule: "When the DCS member channel triggers a member-update approval, the Member Admission Approval system shall retrieve the buffered update record, validate only the supplied fields against the same conditional domain invariants as direct update, and — on success — apply the changes and post back confirmation to DCS." (MemberAdmissionApproval § "Member-Update Approval Flow")
> → ALL 8 category specifications run via the same **private `performConditionalDomainValidation(context)`** helper as `update()`. This is a **separate AR method** from `update()` because it emits `MemberUpdateApprovedEvent` (not `MemberUpdatedEvent`) and carries DCS-specific routing metadata (bufferId, dcsChannel).

```pseudocode
approveMemberUpdate(updateApprovalData):
  // ── Status guard (runs BEFORE spec chain) ────────────────────────────────
  IF this.memberDomainStatus == 2 THEN   // inactive
    THROW MemberValidationException(
      MemberFailedEvent.validationError(updateApprovalData.traceId,
        Map.of(MessageKey.MEMBER.getKey(),
          LocalizedMessage(key = MessageKey.MEMBER_INACTIVE.getKey(),
            args = [this.memberNo]))))
  END IF

  // ── Field merge (non-null fields from DCS buffer data — same as update()) ──
  IF updateApprovalData.memberName is not null THEN this.memberName = updateApprovalData.memberName END IF
  IF updateApprovalData.personalInfo is not null THEN this.personalInfo = this.personalInfo.partialUpdate(updateApprovalData.personalInfo) END IF
  IF updateApprovalData.contactInfo is not null  THEN this.contactInfo  = this.contactInfo.partialUpdate(updateApprovalData.contactInfo) END IF
  IF updateApprovalData.nominees is not null     THEN this.nominees = updateApprovalData.nominees END IF
  // ... merge remaining non-null fields ...
  this.bufferId   = updateApprovalData.bufferId  // DCS-specific: buffer record that triggered this update
  this.uuidNo     = deriveUuidNo(this.personalInfo)
  this.updatedBy  = updateApprovalData.operatorId
  this.lastUpdated = now()

  // ── Validation context (conditional — same structure as update()) ──────────
  context = new MemberValidationContext(
    physicalOfficeInfo    = updateApprovalData.physicalOfficeInfo,
    projectInfo           = updateApprovalData.projectInfo,
    projectPolicyInfo     = updateApprovalData.projectPolicyInfo,
    groupInfo             = updateApprovalData.groupInfo,
    employeeCoreInfo      = updateApprovalData.employeeCoreInfo,
    memberClassification  = updateApprovalData.memberClassification,
    savingsProduct        = updateApprovalData.savingsProduct,
    savingsProductPolicy  = updateApprovalData.savingsProductPolicy,
    relationships         = updateApprovalData.relationships,
    businessDate          = updateApprovalData.businessDate,
    deduplicationResult   = updateApprovalData.deduplicationResult,
    existingNominees      = this.nominees,
    aggregate             = this)

  // ── Conditional 8-spec AND chain ─────────────────────────────────────────
  performConditionalDomainValidation(context, updateApprovalData.traceId)
  // (private helper — same chain as update(); shared to avoid duplication)

  // ── Post-validation nominee redistribution ───────────────────────────────
  IF updateApprovalData.nominees is not null AND nominees is not empty THEN
    equalShare = roundHalfUp(100.0 / nominees.size(), 2)
    FOR EACH nominee IN nominees DO nominee.sharePercent = equalShare END FOR
  END IF

  this.status = DomainStatus.UPDATED
  addEvent(MemberEventMapper.toUpdateApprovedEvent(this))
```

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Module: Member Admission Approval — Member-Update Approval Flow"

---

**Behaviour — delete(deletionData):** [PSEUDOCODE]

> Source rule: "When an operator deletes a member, the Member Management system shall mark the member inactive together with the member's fee transactions, membership history and status-change history, reverse any related automatic vouchers asynchronously…" (MemberManagement § "Member Deletion") and "If the member to be deleted is in closed status, the Member Management system shall reject the deletion." / "If the member has one or more loan proposals… reject." / "If the member has any savings transactions… reject."
> → `delete` has **no domain specification chain**. Only HANDLER_GUARDs: closed status check, office authorisation, loan proposal guard, savings transaction guard. No `MemberValidationContext` constructed, no source data fetched.

```pseudocode
delete(deletionData):
  // ── Status guard — closed member cannot be deleted ────────────────────────
  IF this.closingDate is not null THEN
    THROW MemberValidationException(
      MemberFailedEvent.validationError(deletionData.traceId,
        Map.of(MessageKey.MEMBER.getKey(),
          LocalizedMessage(key = MessageKey.MEMBER_CLOSED.getKey()))))
    // "Can not Delete closed member !!"
  END IF

  // ── Office authorisation guard ────────────────────────────────────────────
  IF this.branchInfoId != deletionData.operatorBranchId THEN
    THROW MemberValidationException(
      MemberFailedEvent.validationError(deletionData.traceId,
        Map.of(MessageKey.MEMBER.getKey(),
          LocalizedMessage(key = MessageKey.MEMBER_DELETE_UNAUTHORIZED.getKey()))))
    // "You are not authorized to delete this member"
  END IF

  // ── Loan proposal guard — checked by handler via external loan service ────
  // (handler injects loanProposalExists flag from loan domain before calling delete())
  IF deletionData.hasLoanProposals THEN
    THROW MemberValidationException(
      MemberFailedEvent.validationError(deletionData.traceId,
        Map.of(MessageKey.MEMBER.getKey(),
          LocalizedMessage(key = MessageKey.MEMBER_HAS_LOAN_PROPOSALS.getKey()))))
    // "Can not Delete because of existing loan process !!"
  END IF

  // ── Savings transaction guard — checked by handler via savings domain ─────
  IF deletionData.hasSavingsTransactions THEN
    THROW MemberValidationException(
      MemberFailedEvent.validationError(deletionData.traceId,
        Map.of(MessageKey.MEMBER.getKey(),
          LocalizedMessage(key = MessageKey.MEMBER_HAS_SAVINGS.getKey()))))
    // "Can not Delete because of existing savings process !!"
  END IF

  // ── Soft delete ───────────────────────────────────────────────────────────
  // No domain specifications run. No MemberValidationContext. No source data fetched.
  this.memberDomainStatus = 2  // inactive
  this.domainStatus       = DomainStatus.UPDATED
  this.updatedBy          = deletionData.operatorId
  this.lastUpdated        = now()
  addEvent(MemberEventMapper.toDeletedEvent(this))
```

---

**Behaviour — activate(activationData):** [PSEUDOCODE]

> Source rule: "When an operator activates one or more members, the Member Management system shall change each selected member's status from inactive to active as of the relevant office's business day, record a membership status-change history for each member…" (MemberManagement § "Member Activation")
> → HANDLER_GUARD: must be inactive. Business day check at presentation/handler layer.

```pseudocode
activate(activationData):
  // ── Status guard ─────────────────────────────────────────────────────────
  IF this.memberDomainStatus != 2 THEN  // not inactive
    THROW MemberValidationException(
      MemberFailedEvent.validationError(activationData.traceId,
        Map.of(MessageKey.MEMBER.getKey(),
          LocalizedMessage(key = MessageKey.MEMBER_NOT_INACTIVE.getKey()))))
  END IF

  this.memberDomainStatus = 1  // active
  this.memberStatusId     = MemberStatus.ACTIVE
  this.domainStatus       = DomainStatus.UPDATED
  this.updatedBy          = activationData.operatorId
  this.lastUpdated        = now()

  statusHistory.add(new MembershipStatusChangeHistory(
    oldMemberStatusId = 2,  // inactive
    memberStatusId    = MemberStatus.ACTIVE,
    statusChangeDate  = activationData.businessDate,
    projectInfoId     = this.projectInfoId,
    groupInfoId       = this.groupInfoId,
    officeInfoId      = this.branchInfoId,
    domainStatus      = DomainStatus.CREATED))

  addEvent(MemberEventMapper.toActivatedEvent(this))
```

---

**Behaviour — saveFamily(familyData):** [PSEUDOCODE]

> Source rule: "When an operator submits a member's family-and-nominee information, the Member Management system shall save the member's nominees, guardian, loan guarantor, family composition, and other-organisation loans, distribute the nominee share equally among the retained nominees…" (MemberManagement § "Member Family Information")
> → `NomineeAndGuarantorSpecification` runs (nominee relationships, guardian requirement for minors, guarantor NID format). No full 8-spec chain.

```pseudocode
saveFamily(familyData):
  // ── Nominee/guarantor validation context ─────────────────────────────────
  context = new MemberValidationContext(
    relationships  = familyData.relationships,  // spec 7
    aggregate      = this)

  compositeSpec = new NomineeAndGuarantorSpecification<>()
  errors = compositeSpec.validate(context)
  IF errors is not empty THEN
    THROW MemberValidationException(
      MemberFailedEvent.validationError(familyData.traceId, errors))
  END IF

  // ── Apply family data ────────────────────────────────────────────────────
  this.nominees      = familyData.nominees
  this.guardianInfo  = familyData.guardianInfo   // null if all nominees ≥ 18
  this.guarantorInfo = familyData.guarantorInfo
  this.familyInfo    = familyData.familyInfo

  // Remove guardian when all nominees ≥ 18
  IF all nominees have age >= 18 THEN this.guardianInfo = null END IF

  // Mark existing guarantor inactive if no guarantor submitted
  IF familyData.guarantorInfo is null AND this.guarantorInfo is not null THEN
    this.guarantorInfo.isActive = false
  END IF

  // Distribute nominee shares
  IF nominees is not empty THEN
    equalShare = roundHalfUp(100.0 / nominees.size(), 2)
    FOR EACH nominee IN nominees DO nominee.sharePercent = equalShare END FOR
  END IF

  this.domainStatus = DomainStatus.UPDATED
  this.updatedBy    = familyData.operatorId
  this.lastUpdated  = now()
  addEvent(MemberEventMapper.toFamilySavedEvent(this))
```

---

**Behaviour — updateFamily(familyData):** [PSEUDOCODE]

> Source rule: "When the standalone family-information form is submitted, the Member Management system shall save the family composition and other-organisation loans…" (MemberManagement § "Member Family Information")
> → Field-level validation only (family form has no data → reject). No domain spec chain.

```pseudocode
updateFamily(familyData):
  IF familyData.familyInfo is null AND familyData.otherOrgLoans is empty THEN
    THROW MemberValidationException(
      MemberFailedEvent.validationError(familyData.traceId,
        Map.of(MessageKey.MEMBER.getKey(),
          LocalizedMessage(key = MessageKey.NO_DATA_TO_SAVE.getKey()))))
    // "No data found to save"
  END IF

  this.familyInfo  = familyData.familyInfo
  this.domainStatus = DomainStatus.UPDATED
  this.updatedBy   = familyData.operatorId
  this.lastUpdated = now()
  addEvent(MemberEventMapper.toFamilyUpdatedEvent(this))
```

---

**Behaviour — updatePhoto(photoData):** [PSEUDOCODE]

> Source rule: "When an operator submits a member's photo, the Member Management system shall store the photo reference on the member's personal information…" (MemberManagement § "Member Photo")
> → No domain spec chain. File relocation handled asynchronously by handler.

```pseudocode
updatePhoto(photoData):
  IF this.personalInfo is null THEN this.personalInfo = new PersonalInfo() END IF
  this.personalInfo.photoReference = photoData.photoReference
  this.domainStatus = DomainStatus.UPDATED
  this.updatedBy    = photoData.operatorId
  this.lastUpdated  = now()
  addEvent(MemberEventMapper.toPhotoUpdatedEvent(this))
```

---

**Behaviour — updateSignature(signatureData):** [PSEUDOCODE]

> Source rule: "When an operator submits a member's signature, the Member Management system shall store the signature reference, relocate the uploaded signature file into the member's permanent storage…" (MemberManagement § "Member Signature")

```pseudocode
updateSignature(signatureData):
  this.signatureReference = signatureData.signatureReference
  this.domainStatus = DomainStatus.UPDATED
  this.updatedBy    = signatureData.operatorId
  this.lastUpdated  = now()
  addEvent(MemberEventMapper.toSignatureUpdatedEvent(this))
```

---

**Behaviour — updateMembershipDocument(documentData):** [PSEUDOCODE]

> Source rule: "When an operator submits a member's documents, the Member Management system shall store the membership-form, national-identity, passport, driving-licence, photo-identity, survey-form and other-form file references under standardised names…" (MemberManagement § "Membership Documents")

```pseudocode
updateMembershipDocument(documentData):
  this.membershipDocuments = new MembershipDocument(
    membershipFormReference   = documentData.membershipFormReference,
    nationalIdReference       = documentData.nationalIdReference,
    passportReference         = documentData.passportReference,
    drivingLicenseReference   = documentData.drivingLicenseReference,
    photoIdReference          = documentData.photoIdReference,
    surveyFormReference       = documentData.surveyFormReference,
    otherFormReference        = documentData.otherFormReference)
  this.domainStatus = DomainStatus.UPDATED
  this.updatedBy    = documentData.operatorId
  this.lastUpdated  = now()
  addEvent(MemberEventMapper.toMembershipDocumentUpdatedEvent(this))
```

---

**Behaviour — updateAssets(assetData):** [PSEUDOCODE]

> Source rule: "When an operator submits a member's asset information, the Member Management system shall add or update the listed assets, remove assets marked deleted…" (MemberManagement § "Member Asset Information")

```pseudocode
updateAssets(assetData):
  // Remove assets flagged for deletion
  this.assets.removeIf(a -> assetData.deletedAssetIds.contains(a.id))
  // Add or update assets from submission
  FOR EACH asset IN assetData.assets DO
    existing = findById(this.assets, asset.id)
    IF existing is null THEN this.assets.add(asset)
    ELSE existing.quantity = asset.quantity; existing.value = asset.value END IF
  END FOR
  this.domainStatus = DomainStatus.UPDATED
  this.updatedBy    = assetData.operatorId
  this.lastUpdated  = now()
  addEvent(MemberEventMapper.toAssetUpdatedEvent(this))
```

---

**Behaviour — reassignProjectOfficer(reassignData):** [PSEUDOCODE]

> Source rule: "When an operator reassigns the project officer for selected members, the Member Management system shall update those members' assigned project officer to the chosen employee…" (MemberManagement § "Member Project-Officer Reassignment")

```pseudocode
reassignProjectOfficer(reassignData):
  this.assignedPoId       = reassignData.newPoId
  this.lastPoAssignedDate = reassignData.businessDate
  this.domainStatus       = DomainStatus.UPDATED
  this.updatedBy          = reassignData.operatorId
  this.lastUpdated        = now()
  addEvent(MemberEventMapper.toProjectOfficerReassignedEvent(this))
```

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management", "Module: Member Activation", "Module: Member Project-Officer Reassignment"
> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Module: Member Admission Approval", "Module: Member Admission Validation Rules"

---

### DDD-REQ-002 — Embedded Entity: PersonalInfo

The Member domain system shall model `PersonalInfo` as an embedded MongoDB sub-document stored inside the `Member` document. It holds the member's demographic and identity-document details.

| Field | Type | Description | Constraints |
|-------|------|-------------|-------------|
| `salutationId` | `String` | Salutation reference | nullable |
| `nationalId` | `String` | National ID number | nullable, 13–17 characters |
| `smartCardId` | `String` | Smart-card identifier | nullable, max 10 |
| `passportNo` | `String` | Passport number | nullable, max 30 |
| `drivingLicenseNo` | `String` | Driving-licence number | nullable, max 30 |
| `photoIdNo` | `String` | Photo identity number | nullable, max 30 |
| `genderId` | `String` | Gender identifier | required |
| `maritalStatusId` | `String` | Marital status identifier (married = "2") | required |
| `age` | `Integer` | Computed age in years | nullable |
| `biometricStatus` | `Integer` | Biometric enrolment status | default 6 |
| `occupationId` | `String` | Occupation reference | nullable |
| `dateOfBirth` | `LocalDate` | Date of birth | nullable |
| `fatherName` | `String` | Father or husband name | nullable, max 50, letters/hyphen/dot/space |
| `motherName` | `String` | Mother name | nullable, max 50, letters/hyphen/dot/space |
| `spouseName` | `String` | Spouse name | nullable, max 50, letters/hyphen/dot/space |
| `spDateOfBirth` | `LocalDate` | Spouse date of birth | nullable |
| `spNationalId` | `String` | Spouse national identity | nullable, 13–17 characters |
| `spSmartCardId` | `String` | Spouse smart-card identifier | nullable, max 10 |
| `spPassportNo` | `String` | Spouse passport number | nullable, max 30 |
| `spPhotoIdNo` | `String` | Spouse photo identity number | nullable, max 30 |
| `bikashWalletNo` | `String` | bKash wallet number | nullable |
| `rocketWalletNo` | `String` | Rocket wallet number | nullable |
| `referralInfoId` | `String` | Referral record identifier | nullable |
| `photoReference` | `String` | Photo file reference | nullable |
| `otherIdTypeId` | `Integer` | Other-identity type: 1=Passport, 2=BirthCert, 3=DrivingLicense | nullable |
| `otherIdTypeNo` | `String` | Other-identity document number | nullable, max 20 |
| `expiryDate` | `LocalDate` | Identity document expiry date | nullable |
| `placeOfIssuingCountry` | `String` | Place or country of issue | nullable |
| `isPersonWithDisability` | `Boolean` | Whether the member has a disability | nullable |

The `PersonalInfo` record shall expose a `partialUpdate(PersonalInfo incoming)` method that returns a new instance merging non-null incoming values with existing values.

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Domain Entities and Properties — Personal Information"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Domain Entities and Properties — PersonalInfo"

---

### DDD-REQ-003 — Embedded Entity: ContactInfo

The Member domain system shall model `ContactInfo` as an embedded MongoDB sub-document inside `Member`. It holds the member's contact details and owns the addresses list.

| Field | Type | Description | Constraints |
|-------|------|-------------|-------------|
| `contactNo` | `String` | Primary contact number | required, max 20, digits/plus/parentheses/hyphen |
| `contactNoOptional` | `String` | Secondary contact number | nullable |
| `addresses` | `List<MemberAddress>` | The member's addresses (cascade all, delete-orphan) | nullable |

The `ContactInfo` record shall expose a `partialUpdate(ContactInfo incoming)` method.

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Domain Entities and Properties — Contact Information"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Domain Entities and Properties — ContactInfo"

---

### DDD-REQ-004 — Embedded Entity: MemberAddress [CONFLICT]

The Member domain system shall model `MemberAddress` as an embedded sub-document inside `ContactInfo`. Field list is the **union** of both EARS files — see Open Questions Q-1 for the definition conflict.

| Field | Type | Description | Constraints |
|-------|------|-------------|-------------|
| `addressTitleId` | `String` | Address type: present (1) or permanent (2) | required |
| `address` | `String` | Free-text address line | nullable |
| `countryId` | `String` | Country reference [MemberManagement adds this field] | nullable |
| `cityId` | `String` | District (city) reference | nullable |
| `thanaId` | `String` | Sub-district (thana/upazila) reference | nullable |
| `zipCode` | `String` | Postal code [MemberManagement adds this field] | nullable, max 8, digits |

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Domain Entities and Properties — Member Address"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Domain Entities and Properties — MemberAddress"
> [CONFLICT: MemberAdmissionApproval omits countryId and zipCode; MemberManagement includes them — union used]

---

### DDD-REQ-005 — Embedded Entity: NomineeInfo

The Member domain system shall model `NomineeInfo` as an embedded sub-document in the `nominees` list of `Member`.

| Field | Type | Description | Constraints |
|-------|------|-------------|-------------|
| `id` | `String` | Nominee identifier (UUID) | required |
| `name` | `String` | Nominee name | required, max 50, letters/hyphen/dot/space |
| `relationshipId` | `String` | Relationship reference | required |
| `sharePercent` | `BigDecimal` | Nomination share percentage | required |
| `dateOfBirth` | `LocalDate` | Nominee date of birth | nullable |
| `age` | `Integer` | Computed nominee age in years | nullable |
| `photoReference` | `String` | Photo file reference | nullable |
| `nationalId` | `String` | Nominee national identity | nullable, 13–17 characters |
| `smartCardId` | `String` | Nominee smart-card identifier | nullable, max 10 |
| `passportNo` | `String` | Nominee passport number | nullable, max 30 |
| `photoIdNo` | `String` | Nominee photo identity number | nullable, max 30 |
| `contactNo` | `String` | Nominee contact number | nullable, max 20, digits/plus/parentheses/hyphen |
| `isDeleted` | `Boolean` | Soft-delete marker (in-memory only; not persisted) | transient |

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Domain Entities and Properties — Nominee Information"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Domain Entities and Properties — NomineeInfo"

---

### DDD-REQ-006 — Embedded Entity: GuardianInfo

The Member domain system shall model `GuardianInfo` as an embedded sub-document inside `Member`. A guardian is required when any nominee is under 18.

| Field | Type | Description | Constraints |
|-------|------|-------------|-------------|
| `guardianName` | `String` | Guardian name | required, max 50, must not begin with a digit |
| `nationalId` | `String` | Guardian national identity | nullable, must be 13 or 17 digits when supplied |
| `dateOfBirth` | `LocalDate` | Guardian date of birth | nullable |
| `age` | `Integer` | Computed guardian age | nullable |
| `address` | `String` | Guardian address | nullable |
| `relationshipId` | `String` | Guardian's relationship to the member | nullable |
| `nomineeId` | `String` | Reference to the minor nominee this guardian is linked to | nullable |

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Domain Entities and Properties — Guardian Information"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Domain Entities and Properties — GuardianInfo"

---

### DDD-REQ-007 — Embedded Entity: GuarantorInfo

The Member domain system shall model `GuarantorInfo` as an embedded sub-document inside `Member`. The guarantor's identity document must be a National ID on new admission.

| Field | Type | Description | Constraints |
|-------|------|-------------|-------------|
| `guarantorName` | `String` | Guarantor name | required, must not begin with a digit |
| `nationalId` | `String` | Guarantor National ID | nullable; must be 13 or 17 digits when supplied |
| `dateOfBirth` | `LocalDate` | Guarantor date of birth | nullable |
| `age` | `Integer` | Computed guarantor age | nullable |
| `relationshipId` | `String` | Guarantor's relationship to the member | nullable |
| `isActive` | `Boolean` | Whether the guarantor record is active | default true |
| `domainStatusId` | `Integer` | Record domain status: 1 = active, 2 = inactive | default 1 |

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Domain Entities and Properties — Guarantor Information"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Domain Entities and Properties — GuarantorInfo"

---

### DDD-REQ-008 — Embedded Entity: PassbookInfo

The Member domain system shall model `PassbookInfo` as an embedded sub-document in the `passbooks` list of `Member`.

| Field | Type | Description | Constraints |
|-------|------|-------------|-------------|
| `id` | `String` | Passbook record identifier (UUID) | required |
| `passbookNo` | `String` | Passbook number | nullable, max 20 |
| `issueDate` | `LocalDate` | Date the passbook was issued | required |
| `passbookStatusId` | `String` | Passbook status reference (Active/Inactive) | required |
| `passbookPrice` | `BigDecimal` | Price charged for the passbook | default 0 |
| `statusChangeDate` | `LocalDate` | Date the status last changed | nullable |
| `transactionNo` | `String` | Associated transaction number | nullable |

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Domain Entities and Properties — Passbook Information"

---

### DDD-REQ-009 — Embedded Entity: FamilyInfo

The Member domain system shall model `FamilyInfo` as an embedded sub-document inside `Member`.

| Field | Type | Description | Constraints |
|-------|------|-------------|-------------|
| `houseHoldHeadName` | `String` | Household head name | nullable, max 50, letters/hyphen/dot/space |
| `sonNo` | `Integer` | Number of sons | nullable |
| `daughterNo` | `Integer` | Number of daughters | nullable |
| `maleNo` | `Integer` | Number of male household members | nullable |
| `femaleNo` | `Integer` | Number of female household members | nullable |
| `earningMemberNo` | `Integer` | Number of earning household members | nullable |
| `otherOrgMemberNo` | `Integer` | Members in other microfinance organisations | nullable |
| `isTaxPayer` | `Boolean` | Whether the family pays tax | nullable |
| `isFamilyLoan` | `Boolean` | Whether the family has loans from other organisations | nullable |
| `otherOrgLoans` | `List<OtherOrganizationLoan>` | Other-organisation loan records (cascade all, delete-orphan) | nullable |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Domain Entities and Properties — FamilyInfo"

---

### DDD-REQ-010 — Embedded Entity: OtherOrganizationLoan

The Member domain system shall model `OtherOrganizationLoan` as an embedded sub-document inside `FamilyInfo`.

| Field | Type | Description | Constraints |
|-------|------|-------------|-------------|
| `id` | `String` | Loan record identifier (UUID) | required |
| `loanAmount` | `BigDecimal` | Loan amount | required, minimum 1 |
| `organizationName` | `String` | Other organisation name | required, max 30, letters/hyphen/dot/space |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Domain Entities and Properties — OtherOrganizationLoan"

---

### DDD-REQ-011 — Embedded Entity: HouseholdAsset

The Member domain system shall model `HouseholdAsset` as an embedded sub-document in the `assets` list of `Member`.

| Field | Type | Description | Constraints |
|-------|------|-------------|-------------|
| `id` | `String` | Asset identifier (UUID) | required |
| `assetName` | `String` | Asset name | required |
| `assetQuantity` | `Integer` | Asset quantity | required, max 4 digits |
| `assetValue` | `BigDecimal` | Asset monetary value | required, max 15 digits |
| `isDeleted` | `Boolean` | Soft-delete marker (in-memory only) | transient |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Asset Information"

---

### DDD-REQ-012 — Value Object: MembershipStatusChangeHistory

The Member domain system shall model `MembershipStatusChangeHistory` as an immutable Java `record` embedded in the `statusHistory` list of `Member`. Each entry is immutable once written.

| Field | Type | Description |
|-------|------|-------------|
| `groupInfoId` | `String` | Group (VO) at the time of the status change (nullable) |
| `projectInfoId` | `String` | Project at the time of the change |
| `officeInfoId` | `String` | Office at the time of the change |
| `statusChangeDate` | `LocalDate` | Date the status changed |
| `memberStatusId` | `String` | New member status identifier (Active = "1") |
| `oldMemberStatusId` | `String` | Previous status identifier (zero status = "0") |
| `domainStatus` | `DomainStatus` | bits.ddd lifecycle status of this entry |
| `createdBy` | `String` | Creating operator |
| `updatedBy` | `String` | Last-updating operator |

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Domain Entities and Properties — Membership Status Change History"

---

### DDD-REQ-013 — Value Object: MembershipDocument

The Member domain system shall model `MembershipDocument` as an immutable Java `record` embedded inside `Member`. It holds file references for all membership-related documents under standardised names.

| Field | Type | Description |
|-------|------|-------------|
| `membershipFormReference` | `String` | Membership form file reference (nullable) |
| `nationalIdReference` | `String` | National identity document file reference (nullable) |
| `passportReference` | `String` | Passport file reference (nullable) |
| `drivingLicenseReference` | `String` | Driving-licence file reference (nullable) |
| `photoIdReference` | `String` | Photo identity file reference (nullable) |
| `surveyFormReference` | `String` | Survey form file reference (nullable) |
| `otherFormReference` | `String` | Other form file reference (nullable) |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Membership Documents"

---

### DDD-REQ-014 — Status Lifecycle: Member

The Member aggregate shall track its lifecycle through the following states using `DomainStatus` from `com.bits.ddd.shared.domain.enums`:

| State | Trigger | Previous States |
|-------|---------|-----------------|
| `CREATED` | `Member.create(...)` | — (initial) |
| `UPDATED` | `Member.update(...)`, `Member.delete()`, `Member.activate()`, `Member.saveFamily()`, `Member.updateFamily()`, `Member.updatePhoto()`, `Member.updateSignature()`, `Member.updateMembershipDocument()`, `Member.updateAssets()`, `Member.reassignProjectOfficer()` | CREATED, UPDATED |

The system shall also define a local `MemberDomainStatus` integer flag:

| Value | Meaning |
|-------|---------|
| `1` | Active (set at registration, after activation) |
| `2` | Inactive (set on deletion, reactivated by activate()) |

The system shall maintain a `memberStatusId` field referencing the `MemberStatus` snapshot. Business statuses (Active, Inactive, Closed, etc.) are tracked in the `MemberStatus` SOURCE_DATA reference.

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Domain Concepts and States — Member Lifecycle Status"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Cross-Cutting Requirements — Audit and Record Lifecycle"

---

### DDD-REQ-015 — Validation Context: MemberValidationContext

The Member domain system shall define a `MemberValidationContext` record in `domain/specification/context/` implementing all specification context interfaces.

```pseudocode
public record MemberValidationContext(
    PhysicalOfficeInfo    physicalOfficeInfo,    // BusinessDayAndBranchSpecification
    ProjectInfo           projectInfo,           // FieldPresenceAndFormat, MemberCategoryAndGroupPolicy
    ProjectPolicyInfo     projectPolicyInfo,     // MemberCategoryAndGroupPolicy, SavingsProduct
    GroupInfo             groupInfo,             // MemberCategoryAndGroupPolicy
    EmployeeCoreInfo      employeeCoreInfo,      // MemberCategoryAndGroupPolicy
    MemberClassification  memberClassification,  // MemberCategoryAndGroupPolicy
    SavingsProduct        savingsProduct,        // SavingsProductSpecification
    SavingsProductPolicy  savingsProductPolicy,  // SavingsProductSpecification
    List<Relationship>    relationships,         // NomineeAndGuarantorSpecification
    LocalDate             businessDate,          // BusinessDayAndBranch, FieldPresenceAndFormat
    DeduplicationResult   deduplicationResult,   // DeduplicationSpecification
    List<NomineeInfo>     existingNominees,      // NomineeAndGuarantorSpecification (update: 3-cap check)
    Member                aggregate)             // all specs
    implements OfficeContext, ProjectContext, PolicyContext, GroupContext,
               ClassificationContext, SavingsContext, DeduplicationContext,
               NomineeContext, BusinessDayContext {}
```

The context is instantiated by the aggregate before running domain specifications on every `create(...)` and `update(...)` call. Fields null in the update context mean the consuming spec null-guards and skips sub-rules for unchanged fields.

> 📎 [INFERRED] — required by bits.ddd pattern

---

### DDD-REQ-016 — Specification: FieldPresenceAndFormatSpecification

The Member domain system shall implement `FieldPresenceAndFormatSpecification` in `domain/specification/rules/`, implementing `Specification<ValidationContext>`. It bundles all required-field presence and format validation rules from the admission and management EARS files.

| Sub-rule | Rejection message | Source |
|----------|-------------------|--------|
| Branch code absent (admission) | "Branch is required" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Project code absent (admission) | "Project is required" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Member category absent (admission) | "Member Category is required" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Member name absent | "Member Name is required" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Member name fails name pattern (letters/spaces/hyphens/dots, must not start with digit) | "'Member Name' format not supported" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Gender absent | "Gender is required" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Date of birth absent | "Date of Birth is required" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Date of birth not a valid calendar date (yyyy-MM-dd) | "'Date of Birth' format not supported" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Occupation absent | "Occupation is required" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Father name absent | "Father Name is required" / "Enter Father Name" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Father name fails name pattern | "'Father Name' format not supported" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Mother name absent | "Mother Name is required" / "Enter Mother Name" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Mother name fails name pattern | "'Mother Name' format not supported" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Mobile number absent | "Mobile Number is required" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Mobile number fails Bangladeshi mobile pattern | "'Mobile Number' format not supported" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Marital status absent | "Marital Status is required" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Married and spouse name absent | "Spouse Name is required" / "Enter Spouse Name for Married Member" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Married and spouse name fails name pattern | "'Spouse Name' format not supported" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Married and spouse date of birth absent (admission) | "Spouse Date of Birth is required" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Married and spouse date of birth bad format | "'Spouse Date of Birth' format not supported" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Permanent address absent | "Permanent Address is required" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Present address absent | "Present Address is required" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Permanent upazila absent | "Permanent Upazila is required" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Present upazila absent | "Present Upazila is required" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Savings product absent | "Savings Product is required" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Target amount absent | "Target Amount is required" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| No address after removing deleted entries | "Address information is required." | MemberManagement § "Module: Member Management — Member Registration" |
| Contact number absent | "Contact number is required" | MemberManagement § "Module: Member Management — Member Registration" |
| Passbook number already in use by another member | "Member {0} of VO {1} is currently using the given {2} passbook…" | MemberManagement § "Module: Member Management — Member Registration" |
| No identity document supplied at all | "User must specify at least one of National ID, Smart card ID, Passport No or Other Photo ID" | MemberManagement § "Module: Member Management — Member Registration" |
| Application date not valid date | "Application date is invalid format" | MemberManagement § "Module: Member Management — Member Registration" |
| Application date later than business date | "Application date should lower than business date" | MemberManagement § "Module: Member Management — Member Registration" |
| Membership date not valid date | "Application date is invalid format" | MemberManagement § "Module: Member Management — Member Registration" |
| Membership date outside app-date–business-date range | "Membership date should between application date and business date" | MemberManagement § "Module: Member Management — Member Registration" |

**Behaviour — validate(context):** [PSEUDOCODE]

```pseudocode
validate(context):
  errors = new HashMap
  ctx = (FieldPresenceContext) context

  // All sub-rules accumulate into errors; none short-circuits within this spec
  IF ctx.aggregate.memberName is blank THEN errors.put(..., "Member Name is required") END IF
  IF ctx.aggregate.memberName fails name pattern THEN errors.put(..., "'Member Name' format not supported") END IF
  IF ctx.aggregate.personalInfo.genderId is blank THEN errors.put(..., "Gender is required") END IF
  IF ctx.aggregate.personalInfo.maritalStatusId is blank THEN errors.put(..., "Marital Status is required") END IF
  IF ctx.aggregate.personalInfo.dateOfBirth is null THEN errors.put(..., "Date of Birth is required") END IF
  IF ctx.aggregate.personalInfo.fatherName is blank THEN errors.put(..., "Father Name is required") END IF
  IF ctx.aggregate.personalInfo.motherName is blank THEN errors.put(..., "Mother Name is required") END IF
  IF ctx.aggregate.personalInfo.maritalStatusId == "2" AND ctx.aggregate.personalInfo.spouseName is blank
    THEN errors.put(..., "Spouse Name is required") END IF
  IF ctx.aggregate.contactInfo.contactNo is blank THEN errors.put(..., "Contact number is required") END IF
  // ... remaining sub-rules for address, passbook, mobile, dates, etc.
  RETURN errors
```

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Required-Field and Format Validation — New Admission Request", "Required-Field, Format and Business Validation — Member-Update Request"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Registration", "Module: Member Management — Member Profile Update"

---

### DDD-REQ-017 — Specification: IdentityDocumentSpecification

The Member domain system shall implement `IdentityDocumentSpecification` in `domain/specification/rules/`. It validates the structural rules of identity documents for the member, spouse, each guarantor, and each nominee.

| Sub-rule | Rejection message | Source |
|----------|-------------------|--------|
| Identity document object absent for a party | "\<Actor\> ID Card is required" | MemberAdmissionApproval § "Identity-Document Structure Validation — Per Party" |
| Identity document carries no card type | "\<Actor\> Card Type Required" | MemberAdmissionApproval § "Identity-Document Structure Validation — Per Party" |
| Identity document carries no number | "\<Actor\> Card No Required" | MemberAdmissionApproval § "Identity-Document Structure Validation — Per Party" |
| National ID not 13 or 17 digits | "\<Actor\> National ID must be 13/17 digit" / "National ID must be 13/17 digit" | MemberAdmissionApproval § "Identity-Document Structure Validation — Per Party", MemberManagement § "Cross-Cutting Requirements — Error Response Format" |
| Smart Card not exactly 10 digits | "\<Actor\> Smart Card ID must be 10 digit" | MemberAdmissionApproval § "Identity-Document Structure Validation — Per Party" |
| Other identity number > 20 characters | "\<Actor\> Other Id Number Length is incorrect, it should be less then 20 characters." | MemberAdmissionApproval § "Identity-Document Structure Validation — Per Party" |
| Passport without valid expiry date | "\<Actor\> Passport Expiry Date Required or Format not correct" | MemberAdmissionApproval § "Identity-Document Structure Validation — Per Party" |

**Behaviour — validate(context):** [PSEUDOCODE]

```pseudocode
validate(context):
  errors = new HashMap
  ctx = (IdentityDocumentContext) context

  // Validate member identity document
  validatePartyDocument(ctx.aggregate.personalInfo, "Member", errors)

  // Validate spouse document when married
  IF ctx.aggregate.personalInfo.maritalStatusId == "2" THEN
    validateSpouseDocument(ctx.aggregate.personalInfo, errors)
  END IF

  // Validate each guarantor identity document
  IF ctx.aggregate.guarantorInfo is not null THEN
    validatePartyNationalId(ctx.aggregate.guarantorInfo.nationalId, "Guarantor", errors)
  END IF

  // Validate each nominee identity document
  FOR EACH nominee IN ctx.aggregate.nominees DO
    validatePartyDocument(nominee, "Nominee", errors)
  END FOR

  RETURN errors

validatePartyDocument(party, actorLabel, errors):
  IF party NID not null AND (NID.length != 13 AND NID.length != 17) THEN
    errors.put(actorLabel + ".nid", actorLabel + " National ID must be 13/17 digit")
  END IF
  IF party smartCardId not null AND smartCardId.length != 10 THEN
    errors.put(actorLabel + ".smartCard", actorLabel + " Smart Card ID must be 10 digit")
  END IF
  IF party otherIdTypeNo not null AND otherIdTypeNo.length > 20 THEN
    errors.put(actorLabel + ".otherId", actorLabel + " Other Id Number Length is incorrect...")
  END IF
  IF party passportNo not null AND (expiryDate is null OR not valid date) THEN
    errors.put(actorLabel + ".passport", actorLabel + " Passport Expiry Date Required or Format not correct")
  END IF
```

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Identity-Document Structure Validation — Per Party"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Cross-Cutting Requirements — Error Response Format"

---

### DDD-REQ-018 — Specification: BusinessDayAndBranchSpecification

The Member domain system shall implement `BusinessDayAndBranchSpecification` in `domain/specification/rules/`. It validates office type, business day, and date constraints that require knowledge of the branch business date.

| Sub-rule | Rejection message | Source |
|----------|-------------------|--------|
| Office is not a Branch Office | "Member creation only possible at Branch Office" | MemberAdmissionApproval § "Business Validation — New Admission Request"; MemberManagement § "Module: Member Management — Member Registration" |
| Branch business day not open | "Business Day of this office is not open" | MemberAdmissionApproval § "Business Validation — New Admission Request" |
| Application date later than branch business date | "Application date should be lower than or equal to business date." | MemberAdmissionApproval § "Business Validation — New Admission Request" |
| Date of birth on or after business date | "Date of Birth must be previous to business date" | MemberAdmissionApproval § "Business Validation — New Admission Request"; MemberManagement § "Module: Member Management — Member Registration" |

**Behaviour — validate(context):** [PSEUDOCODE]

```pseudocode
validate(context):
  errors = new HashMap
  ctx = (BusinessDayContext) context

  IF ctx.physicalOfficeInfo is null THEN RETURN errors END IF

  IF ctx.physicalOfficeInfo.officeType != BRANCH_OFFICE THEN
    errors.put(MessageKey.OFFICE.getKey(), LocalizedMessage(key = "member.branch.only"))
  END IF

  IF ctx.physicalOfficeInfo.businessDayStatus != OPEN THEN
    errors.put(MessageKey.BUSINESS_DAY.getKey(), LocalizedMessage(key = "member.business.day.not.open"))
  END IF

  IF ctx.aggregate.applicationDate isAfter ctx.businessDate THEN
    errors.put(MessageKey.APP_DATE.getKey(), LocalizedMessage(key = "member.app.date.invalid"))
  END IF

  IF ctx.aggregate.personalInfo.dateOfBirth isAfter or isEqual ctx.businessDate THEN
    errors.put(MessageKey.DOB.getKey(), LocalizedMessage(key = "member.dob.must.be.before.business.date"))
  END IF

  RETURN errors
```

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Business Validation — New Admission Request"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Cross-Cutting Requirements — Authentication and Authorisation"

---

### DDD-REQ-019 — Specification: MemberCategoryAndGroupPolicySpecification

The Member domain system shall implement `MemberCategoryAndGroupPolicySpecification` in `domain/specification/rules/`. It validates member category eligibility, age range, group/officer assignment, and gender policy.

| Sub-rule | Rejection message | Source |
|----------|-------------------|--------|
| Project not found or category not valid for project | "Request Parameter Field 'Category' is not valid" | MemberAdmissionApproval § "Business Validation — New Admission Request" |
| Member age outside category age range (ageFrom–ageTo) | "Member category does not allow specified member age" | MemberAdmissionApproval § "Business Validation — New Admission Request" |
| Group-associated project: group not supplied | "Request Parameter Field 'VO' is not provided" / "VO information is mandatory" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request"; MemberManagement § "Module: Member Management — Member Registration" |
| Group not valid or not in the branch | "Request Parameter Field 'VO' is not valid" | MemberAdmissionApproval § "Business Validation — New Admission Request" |
| Group gender policy disallows member's gender | "VO policy does not allow specified gender" | MemberAdmissionApproval § "Business Validation — New Admission Request" |
| Group not Active status | "Request Parameter Field 'VO' is not Active" | MemberAdmissionApproval § "Business Validation — New Admission Request" |
| Direct-association project: field officer not supplied | "Request Parameter Field 'PO' is not provided" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Field officer not assignable to branch/project | "Invalid PO!" | MemberAdmissionApproval § "Business Validation — New Admission Request" |

**Behaviour — validate(context):** [PSEUDOCODE]

```pseudocode
validate(context):
  errors = new HashMap
  ctx = (ClassificationContext) context

  IF ctx.memberClassification is null THEN
    errors.put(MessageKey.CATEGORY.getKey(), LocalizedMessage(key = "member.category.invalid"))
    RETURN errors  // cannot proceed without classification
  END IF

  // Age range check
  age = computeAge(ctx.aggregate.personalInfo.dateOfBirth, ctx.businessDate)
  IF age < ctx.memberClassification.ageFrom OR age > ctx.memberClassification.ageTo THEN
    errors.put(MessageKey.CATEGORY.getKey(), LocalizedMessage(key = "member.category.age.invalid"))
  END IF

  // Group vs. direct association routing
  IF ctx.projectPolicyInfo.associationType == GROUP THEN
    IF ctx.groupInfo is null THEN
      errors.put(MessageKey.GROUP.getKey(), LocalizedMessage(key = "member.vo.mandatory"))
    ELSE
      IF ctx.groupInfo.branchInfoId != ctx.aggregate.branchInfoId THEN
        errors.put(MessageKey.GROUP.getKey(), LocalizedMessage(key = "member.vo.invalid"))
      END IF
      IF ctx.groupInfo.groupStatus != ACTIVE THEN
        errors.put(MessageKey.GROUP.getKey(), LocalizedMessage(key = "member.vo.not.active"))
      END IF
      IF ctx.groupInfo.applicableGender != BOTH AND ctx.groupInfo.applicableGender != ctx.aggregate.personalInfo.genderId THEN
        errors.put(MessageKey.GENDER.getKey(), LocalizedMessage(key = "member.vo.gender.policy"))
      END IF
    END IF
  ELSE IF ctx.projectPolicyInfo.associationType == MEMBER THEN
    IF ctx.employeeCoreInfo is null THEN
      errors.put(MessageKey.PO.getKey(), LocalizedMessage(key = "member.po.required"))
    ELSE IF ctx.employeeCoreInfo not assignable to branch AND project THEN
      errors.put(MessageKey.PO.getKey(), LocalizedMessage(key = "member.po.invalid"))
    END IF
  END IF

  RETURN errors
```

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Business Validation — New Admission Request", "Required-Field and Format Validation — New Admission Request"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Registration"

---

### DDD-REQ-020 — Specification: SavingsProductSpecification

The Member domain system shall implement `SavingsProductSpecification` in `domain/specification/rules/`. It validates savings product availability and target amount constraints.

| Sub-rule | Rejection message | Source |
|----------|-------------------|--------|
| Savings product not found in snapshot | "Request Parameter Field 'Savings Product' is not valid" | MemberAdmissionApproval § "Business Validation — New Admission Request" |
| Savings product not available for applicable collection frequency | "Request Parameter Field 'Savings Product' is not valid" | MemberAdmissionApproval § "Business Validation — New Admission Request" |
| Target amount below minimum savings installment | "Request Parameter Field 'Target Amount' is not valid. Target amount can not less than minimum target amount" / "Target amount can not less than minimum target amount" | MemberAdmissionApproval § "Business Validation — New Admission Request"; MemberManagement § "Module: Member Management — Member Registration" |
| Savings product changed while savings-account transactions exist (update only) | "Member has already savings account transaction" | MemberAdmissionApproval § "Required-Field, Format and Business Validation — Member-Update Request"; MemberManagement § "Module: Member Management — Member Profile Update" |

**Behaviour — validate(context):** [PSEUDOCODE]

```pseudocode
validate(context):
  errors = new HashMap
  ctx = (SavingsContext) context

  IF ctx.savingsProduct is null THEN
    errors.put(MessageKey.SAVINGS_PRODUCT.getKey(), LocalizedMessage(key = "member.savings.product.invalid"))
    RETURN errors
  END IF

  // Collection frequency check
  IF ctx.savingsProduct NOT available for ctx.projectPolicyInfo.collectionFrequency THEN
    errors.put(MessageKey.SAVINGS_PRODUCT.getKey(), LocalizedMessage(key = "member.savings.product.frequency.invalid"))
  END IF

  // Minimum target amount check
  minTarget = ctx.savingsProductPolicy.minDepositAmount // minimum installment
  IF ctx.aggregate.targetAmount < minTarget THEN
    errors.put(MessageKey.TARGET_AMOUNT.getKey(),
      LocalizedMessage(key = "member.target.amount.below.minimum"))
  END IF

  // Update: block savings product change if transactions exist (injected via context)
  IF ctx.isUpdate AND ctx.savingsProductChanged AND ctx.hasSavingsTransactions THEN
    errors.put(MessageKey.SAVINGS_PRODUCT.getKey(),
      LocalizedMessage(key = "member.savings.account.has.transactions"))
  END IF

  RETURN errors
```

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Business Validation — New Admission Request", "Required-Field, Format and Business Validation — Member-Update Request"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Registration", "Module: Member Management — Member Profile Update"

---

### DDD-REQ-021 — Specification: DeduplicationSpecification

The Member domain system shall implement `DeduplicationSpecification` in `domain/specification/rules/`. It validates cross-organisation identity uniqueness using results from the external de-duplication service (pre-fetched by the handler before calling the aggregate).

| Sub-rule | Rejection message | Source |
|----------|-------------------|--------|
| National ID already registered to another member | "Same National ID already exists for another member." | MemberAdmissionApproval § "Business Validation — New Admission Request"; MemberManagement § "Module: Member Validation Rules" |
| Smart Card ID already registered to another member | "Same Smart Card ID already exists for another member." | MemberAdmissionApproval § "Business Validation — New Admission Request"; MemberManagement § "Module: Member Validation Rules" |
| Driving-licence / photo identity already registered to another member | "Same Birth Certificate Number already exists for another member." | MemberAdmissionApproval § "Business Validation — New Admission Request" |
| Passport already used by another member | Duplicate passport message from de-duplication service | MemberAdmissionApproval § "Business Validation — New Admission Request" |
| Birth certificate already used by another member | Duplicate birth-certificate message from de-duplication service | MemberAdmissionApproval § "Business Validation — New Admission Request" |
| Spouse National ID already registered as another member's spouse | "A spouse is already exist with given spouse national id" | MemberAdmissionApproval § "Business Validation — New Admission Request" |
| De-duplication service call failed | "DeDupe API Error. \n" + underlying error | MemberManagement § "Module: Member Management — Member Registration" |
| Duplicate member found (general) | "Duplicate Information Found" | MemberManagement § "Module: Member Management — Member Registration" |
| Duplicate member found while attempting identity change (non-admin) | "Duplicate Information Found. You Can Not Change any of National ID/Birth Certificate Number/Passport No/Smart Card ID/Driving License No" | MemberManagement § "Module: Member Management — Member Profile Update" |

**Behaviour — validate(context):** [PSEUDOCODE]

```pseudocode
validate(context):
  errors = new HashMap
  ctx = (DeduplicationContext) context

  // De-duplication result pre-fetched by handler; null means no check needed (update: no identity change)
  IF ctx.deduplicationResult is null THEN RETURN errors END IF

  IF ctx.deduplicationResult.failed THEN
    errors.put(MessageKey.DEDUPE.getKey(),
      LocalizedMessage(key = "member.dedupe.api.error", args = [ctx.deduplicationResult.errorDetail]))
    RETURN errors
  END IF

  IF ctx.deduplicationResult.nationalIdDuplicate THEN
    errors.put(MessageKey.NATIONAL_ID.getKey(), LocalizedMessage(key = "member.nid.duplicate"))
  END IF
  IF ctx.deduplicationResult.smartCardDuplicate THEN
    errors.put(MessageKey.SMART_CARD.getKey(), LocalizedMessage(key = "member.smart.card.duplicate"))
  END IF
  IF ctx.deduplicationResult.otherIdDuplicate THEN
    errors.put(MessageKey.OTHER_ID.getKey(), LocalizedMessage(key = "member.other.id.duplicate"))
  END IF
  IF ctx.deduplicationResult.spouseNidDuplicate THEN
    errors.put(MessageKey.SPOUSE_NID.getKey(), LocalizedMessage(key = "member.spouse.nid.duplicate"))
  END IF
  IF ctx.deduplicationResult.generalDuplicate AND ctx.isNonAdminIdentityChange THEN
    errors.put(MessageKey.DEDUPE.getKey(),
      LocalizedMessage(key = "member.dedupe.no.change.allowed"))
  END IF

  RETURN errors
```

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Business Validation — New Admission Request", "Module: Sub-Validators — Identity Uniqueness Check"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Registration", "Module: Member Validation Rules"

---

### DDD-REQ-022 — Specification: NomineeAndGuarantorSpecification

The Member domain system shall implement `NomineeAndGuarantorSpecification` in `domain/specification/rules/`. It validates nominee counts, relationships, guardian requirements, and guarantor rules.

| Sub-rule | Rejection message | Source |
|----------|-------------------|--------|
| Nominee name empty | "Request Parameter Field 'Nominee Name' can not be empty" / Nominee 'name' is required! | MemberAdmissionApproval § "Nominee Field Validation — New Admission Request", "Business Validation — New Admission Request" |
| Nominee relationship not valid | "Request Parameter Field 'Nominee Relationship' is not valid" | MemberAdmissionApproval § "Business Validation — New Admission Request" |
| Nominee relationship is spouse while member unmarried | "Relationship can not be spouse while member is unmarried for Nominee" / "Relationship can not be spouse while member is unmarried" | MemberAdmissionApproval § "Business Validation — New Admission Request"; MemberManagement § "Module: Member Management — Member Family Information" |
| Update: adding a 4th nominee while 3 already exist | "All 3 nominees are created. You can not add more" | MemberAdmissionApproval § "Required-Field, Format and Business Validation — Member-Update Request" |
| Update: referenced existing nominee not found | "Existing nominee not found with provided information" | MemberAdmissionApproval § "Required-Field, Format and Business Validation — Member-Update Request" |
| Nominee under 18 with no guardian supplied | "Guardian is required for nominee with age below 18" | MemberManagement § "Module: Member Management — Member Family Information" |
| Guardian NID not 13 or 17 digits | "National ID must be 13/17 digit" | MemberManagement § "Module: Member Management — Member Family Information" |
| Guarantor relationship not valid | "Request Parameter Field 'Guarantor Relationship' is not valid" | MemberAdmissionApproval § "Business Validation — New Admission Request" |
| Guarantor relationship is spouse while member unmarried | "Relationship can not be spouse while member is unmarried for Guarantor" | MemberAdmissionApproval § "Business Validation — New Admission Request" |
| Guarantor NID not valid format (admission: must be NID) | "Request Parameter Field 'Guarantor National ID' is not valid" | MemberAdmissionApproval § "Business Validation — New Admission Request" |
| Guarantor NID not 13 or 17 digits | "National ID must be 13/17 digit" | MemberManagement § "Module: Member Management — Member Family Information" |

**Behaviour — validate(context):** [PSEUDOCODE]

```pseudocode
validate(context):
  errors = new HashMap
  ctx = (NomineeContext) context

  SPOUSE_REL_ID = "20"  // spouse relationship value
  UNMARRIED_STATUSES = ["1", "3", "4"]  // all non-married marital status ids

  // Nominee count cap on update
  IF ctx.isUpdate AND ctx.existingNominees.size() == 3
     AND ctx.aggregate.nominees contains new nominees THEN
    errors.put(MessageKey.NOMINEE.getKey(), LocalizedMessage(key = "member.nominee.max.reached"))
  END IF

  FOR EACH nominee IN ctx.aggregate.nominees DO
    IF nominee.name is blank THEN
      errors.put("nominee.name." + nominee.id, LocalizedMessage(key = "member.nominee.name.required"))
    END IF
    IF nominee.relationshipId NOT in ctx.validRelationshipIds THEN
      errors.put("nominee.rel." + nominee.id, LocalizedMessage(key = "member.nominee.relationship.invalid"))
    END IF
    IF nominee.relationshipId == SPOUSE_REL_ID
       AND ctx.aggregate.personalInfo.maritalStatusId IN UNMARRIED_STATUSES THEN
      errors.put("nominee.rel." + nominee.id, LocalizedMessage(key = "member.nominee.spouse.unmarried"))
    END IF
  END FOR

  // Guardian required for any nominee under 18
  hasMinorNominee = ctx.aggregate.nominees.any(n -> n.age != null AND n.age < 18)
  IF hasMinorNominee AND ctx.aggregate.guardianInfo is null THEN
    errors.put(MessageKey.GUARDIAN.getKey(), LocalizedMessage(key = "member.guardian.required.for.minor"))
  END IF

  // Guardian NID format
  IF ctx.aggregate.guardianInfo is not null AND ctx.aggregate.guardianInfo.nationalId is not null THEN
    nid = ctx.aggregate.guardianInfo.nationalId
    IF nid.length != 13 AND nid.length != 17 THEN
      errors.put(MessageKey.GUARDIAN_NID.getKey(), LocalizedMessage(key = "member.nid.format.invalid"))
    END IF
  END IF

  // Guarantor validation
  IF ctx.aggregate.guarantorInfo is not null THEN
    g = ctx.aggregate.guarantorInfo
    IF ctx.isAdmission AND g.nationalId is null THEN
      errors.put(MessageKey.GUARANTOR_NID.getKey(), LocalizedMessage(key = "member.guarantor.nid.required"))
    END IF
    IF g.nationalId is not null AND (g.nationalId.length != 13 AND g.nationalId.length != 17) THEN
      errors.put(MessageKey.GUARANTOR_NID.getKey(), LocalizedMessage(key = "member.nid.format.invalid"))
    END IF
    IF g.relationshipId is not null AND g.relationshipId NOT in ctx.validRelationshipIds THEN
      errors.put(MessageKey.GUARANTOR_REL.getKey(), LocalizedMessage(key = "member.guarantor.relationship.invalid"))
    END IF
    IF g.relationshipId == SPOUSE_REL_ID AND ctx.aggregate.personalInfo.maritalStatusId IN UNMARRIED_STATUSES THEN
      errors.put(MessageKey.GUARANTOR_REL.getKey(), LocalizedMessage(key = "member.guarantor.spouse.unmarried"))
    END IF
  END IF

  RETURN errors
```

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Nominee Field Validation — New Admission Request", "Business Validation — New Admission Request", "Required-Field, Format and Business Validation — Member-Update Request"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Family Information"

---

### DDD-REQ-023 — Specification: PersonalDataConsistencySpecification

The Member domain system shall implement `PersonalDataConsistencySpecification` in `domain/specification/rules/`. It validates cross-entity consistency checks for identity documents and bank/ShopUp requirements.

| Sub-rule | Rejection message | Source |
|----------|-------------------|--------|
| Member and spouse share same identity document type+number | "Member and Spouse \<Card Type\> can not be the same!" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| Member and a guarantor share same identity document type+number | "Member and Guarantors \<Card Type\> can not be the same!" | MemberAdmissionApproval § "Required-Field and Format Validation — New Admission Request" |
| ShopUp member missing required bank information | "For ShopUp member Bank Information is mandatory. Please check your inputs" | MemberManagement § "Module: Member Management — Member Registration" |
| Bank account/routing supplied without bank branch | "Please select Bank Name and Bank branch name. Without this Information you can't give member Bank Account Number and Routing number." | MemberManagement § "Module: Member Management — Member Registration" |
| Member identity not unique with insurer (update) | Insurer-provided message | MemberManagement § "Module: Member Management — Member Profile Update" |

**Behaviour — validate(context):** [PSEUDOCODE]

```pseudocode
validate(context):
  errors = new HashMap
  ctx = (ConsistencyContext) context
  p = ctx.aggregate.personalInfo

  // Member vs. spouse identity cross-check
  IF ctx.aggregate.guarantorInfo is not null THEN
    FOR EACH idType IN [NATIONAL_ID, SMART_CARD, PASSPORT, OTHER] DO
      IF memberDocNo(p, idType) is not null
         AND memberDocNo(p, idType) == spouseDocNo(p, idType) THEN
        errors.put(MessageKey.IDENTITY_CONFLICT.getKey(),
          LocalizedMessage(key = "member.spouse.same.id",
            args = [cardTypeName(idType)]))
      END IF
    END FOR
  END IF

  // Member vs. guarantor identity cross-check
  IF ctx.aggregate.guarantorInfo is not null THEN
    FOR EACH idType IN [NATIONAL_ID] DO  // guarantor only has NID in admission flow
      IF memberDocNo(p, idType) is not null
         AND memberDocNo(p, idType) == ctx.aggregate.guarantorInfo.nationalId THEN
        errors.put(MessageKey.IDENTITY_CONFLICT.getKey(),
          LocalizedMessage(key = "member.guarantor.same.id", args = [cardTypeName(idType)]))
      END IF
    END FOR
  END IF

  // Bank info consistency
  IF (ctx.aggregate.bankAccountNumber is not null OR ctx.aggregate.routingNumber is not null)
     AND ctx.aggregate.bankBranchId is null THEN
    errors.put(MessageKey.BANK.getKey(), LocalizedMessage(key = "member.bank.branch.required"))
  END IF

  RETURN errors
```

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Required-Field and Format Validation — New Admission Request"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Registration", "Module: Member Management — Member Profile Update"

---

### DDD-REQ-024 — Specification-to-Behavior Mapping and Composition

#### 24a — Behavior Applicability Matrix

The table below states, for each category specification, whether it runs during each AR behavior, together with the source-EARS basis for its inclusion or exclusion.

| # | Specification | `create` | `update` | `delete` | `activate` | `saveFamily` | `update*`(7 simple) | Source EARS basis |
|---|--------------|:--------:|:--------:|:--------:|:----------:|:------------:|:-------------------:|-------------------|
| 1 | `FieldPresenceAndFormatSpecification` | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | "accumulate all field-presence and format failures" (Admission § Required-Field); "validate only the fields that are supplied" (Admission § Member-Update) |
| 2 | `IdentityDocumentSpecification` | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | Identity doc structure rules in Admission § "Identity-Document Structure Validation — Per Party" |
| 3 | `BusinessDayAndBranchSpecification` | ✅ | ✅ | ❌ | ✅(day only) | ❌ | ❌ | "member registration shall require the branch business day to be open" (Management § Cross-Cutting — AuthN/AuthZ) |
| 4 | `MemberCategoryAndGroupPolicySpecification` | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | Admission § "Business Validation — New Admission Request"; Management § "Member Registration" |
| 5 | `SavingsProductSpecification` | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | Admission § "Business Validation — New Admission Request"; Management § savings product change guard |
| 6 | `DeduplicationSpecification` | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | Admission § "Business Validation — New Admission Request"; Management § de-duplication on registration and update |
| 7 | `NomineeAndGuarantorSpecification` | ✅ | ✅ | ❌ | ❌ | ✅ | ❌ | Admission § "Nominee Field Validation", "Business Validation"; Management § "Member Family Information" |
| 8 | `PersonalDataConsistencySpecification` | ✅ | ✅ | ❌ | ❌ | ❌ | ❌ | Admission § "Required-Field and Format Validation — New Admission Request" (member/spouse/guarantor doc cross-check) |

`delete` — no specification chain: only HANDLER_GUARD (closed status, office auth, loan proposals, savings transactions); no `MemberValidationContext` constructed.
`activate` — only business day check (handled via `BusinessDayAndBranchSpecification` called from handler); no full spec chain.
All other behaviors (7 simple behaviors) — field-level validation only at presentation layer; no domain spec chain.

---

#### 24b — Context Initialisation per Behavior

| Context data | Required by specs | `create` | `update` | `delete` | `saveFamily` |
|-------------|-------------------|:--------:|:--------:|:--------:|:------------:|
| `physicalOfficeInfo` | 3 | ✅ always | ✅ always (re-fetched) | ❌ | ❌ |
| `projectInfo` | 1, 4 | ✅ always | ✅ always | ❌ | ❌ |
| `projectPolicyInfo` | 4, 5 | ✅ always | ✅ always | ❌ | ❌ |
| `groupInfo` | 4 | ✅ always | ✅ when group not changed (via existing member) | ❌ | ❌ |
| `employeeCoreInfo` | 4 | ✅ when direct-association | ✅ when assignedPoId updated | ❌ | ❌ |
| `memberClassification` | 4 | ✅ always | ✅ when memberClassificationId updated | ❌ | ❌ |
| `savingsProduct` | 5 | ✅ always | ✅ when savingsProductId updated | ❌ | ❌ |
| `savingsProductPolicy` | 5 | ✅ always | ✅ when savingsProductId updated | ❌ | ❌ |
| `relationships` | 7 | ✅ when nominees/guarantor | ✅ when nominees/guarantor updated | ❌ | ✅ always |
| `businessDate` | 1, 3 | ✅ always | ✅ always | ❌ | ❌ |
| `deduplicationResult` | 6 | ✅ always | ✅ when identity docs changed | ❌ | ❌ |
| `existingNominees` | 7 | ❌ | ✅ (3-nominee cap check) | ❌ | ✅ always |
| `aggregate` | all | ✅ | ✅ | ❌ | ✅ |

For `update`, null source-data entries mean the consuming spec null-guards and skips sub-rules for unchanged fields.

---

#### 24c — AND Chain (canonical form, applies to both `create` and `update`)

```pseudocode
// CREATE: strict AND chain — all 8 specs run, all violations accumulated
compositeSpec =
  new FieldPresenceAndFormatSpecification<>()           // 1
      .and(new IdentityDocumentSpecification<>())        // 2
      .and(new BusinessDayAndBranchSpecification<>())    // 3
      .and(new MemberCategoryAndGroupPolicySpecification<>()) // 4
      .and(new SavingsProductSpecification<>())          // 5
      .and(new DeduplicationSpecification<>())           // 6
      .and(new NomineeAndGuarantorSpecification<>())     // 7
      .and(new PersonalDataConsistencySpecification<>()) // 8

errors = compositeSpec.validate(context)
IF errors is not empty THEN
  THROW MemberValidationException(
    MemberFailedEvent.validationError(traceId, errors))
END IF

// UPDATE: same chain with conditional context (fail-fast on first spec error returned)
// Specs null-guard inputs and skip sub-rules for unchanged fields
```

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Required-Field and Format Validation — New Admission Request", "Business Validation — New Admission Request", "Required-Field, Format and Business Validation — Member-Update Request"
> 📎 [INFERRED for composition order]

---

### DDD-REQ-025 — Domain Events: Member

The `Member` aggregate shall emit the following domain events via `addEvent(...)` **inside each respective AR behavior method** at the end of each successful state transition. All event classes are sourced from `com.bits.ddd.contracts.member.event.*` and shall NOT be defined locally. Event mapping shall be delegated to `MemberEventMapper` in `domain/mapper/`.

> **Event origination rule:** The "AR Method" column below is the **only place** `addEvent(...)` is called for that event. Command Handlers (`CreateMemberCommandHandler`, `ApproveMemberAdmissionCommandHandler`, etc.) MUST NEVER call `addEvent(...)` — they only call `messagingProcessor.publish(member.getEvents())` after persistence. See DDD-REQ-001a for the complete AR Behavior Map.

| State Transition | AR Method (origin of `addEvent(...)`) | Domain Event Class |
|-----------------|--------------------------------------|-------------------|
| create → CREATED | `.create(creationData)` | `MemberCreatedEvent` |
| approveMemberAdmission → CREATED | `.approveMemberAdmission(admissionData)` | `MemberAdmissionApprovedEvent` |
| update → UPDATED | `.update(updateData)` | `MemberUpdatedEvent` |
| approveMemberUpdate → UPDATED | `.approveMemberUpdate(updateApprovalData)` | `MemberUpdateApprovedEvent` |
| delete → inactive | `.delete(deletionData)` | `MemberDeletedEvent` |
| activate → active | `.activate(activationData)` | `MemberActivatedEvent` |
| saveFamily → UPDATED | `.saveFamily(familyData)` | `MemberFamilySavedEvent` |
| updateFamily → UPDATED | `.updateFamily(familyData)` | `MemberFamilyUpdatedEvent` |
| updatePhoto → UPDATED | `.updatePhoto(photoData)` | `MemberPhotoUpdatedEvent` |
| updateSignature → UPDATED | `.updateSignature(signatureData)` | `MemberSignatureUpdatedEvent` |
| updateMembershipDocument → UPDATED | `.updateMembershipDocument(docData)` | `MembershipDocumentUpdatedEvent` |
| updateAssets → UPDATED | `.updateAssets(assetData)` | `MemberAssetUpdatedEvent` |
| reassignProjectOfficer → UPDATED | `.reassignProjectOfficer(reassignData)` | `MemberProjectOfficerReassignedEvent` |

**Event field schema — MemberCreatedEvent / MemberUpdatedEvent (shared fields):**

| Field | Type | Description |
|-------|------|-------------|
| `memberId` | `String` | Aggregate ID (UUID) |
| `memberNo` | `String` | Auto-generated member number |
| `memberName` | `String` | Full member name |
| `projectInfoId` | `String` | Project reference |
| `branchInfoId` | `String` | Branch office reference |
| `groupInfoId` | `String` | Group reference (nullable) |
| `assignedPoId` | `String` | Field officer reference (nullable) |
| `memberStatusId` | `String` | Current member status |
| `memberClassificationId` | `String` | Category reference |
| `countryId` | `String` | Country reference |
| `nationalId` | `String` | National identity (nullable) |
| `smartCardId` | `String` | Smart-card identifier (nullable) |
| `genderId` | `String` | Gender |
| `dateOfBirth` | `LocalDate` | Date of birth |
| `membershipDate` | `LocalDate` | Membership effective date |
| `applicationDate` | `LocalDate` | Application date |
| `domainStatus` | `DomainStatus` | bits.ddd lifecycle status |
| `apiDataSourceId` | `Integer` | Channel origin (nullable) |
| `traceId` | `String` | Correlation trace ID |
| `eventTimestamp` | `LocalDateTime` | Event emission time |

**Event field schema — MemberDeletedEvent:**

| Field | Type | Description |
|-------|------|-------------|
| `memberId` | `String` | Aggregate ID |
| `memberNo` | `String` | Member number |
| `branchInfoId` | `String` | Branch reference |
| `projectInfoId` | `String` | Project reference |
| `deletedBy` | `String` | Deleting operator |
| `traceId` | `String` | Correlation trace ID |
| `eventTimestamp` | `LocalDateTime` | Event emission time |

**Event field schema — MemberActivatedEvent:**

| Field | Type | Description |
|-------|------|-------------|
| `memberId` | `String` | Aggregate ID |
| `memberNo` | `String` | Member number |
| `memberStatusId` | `String` | New status (Active) |
| `activationDate` | `LocalDate` | Business date of activation |
| `branchInfoId` | `String` | Branch reference |
| `traceId` | `String` | Correlation trace ID |
| `eventTimestamp` | `LocalDateTime` | Event emission time |

**Event field schema — MemberFamilySavedEvent / MemberFamilyUpdatedEvent:**

| Field | Type | Description |
|-------|------|-------------|
| `memberId` | `String` | Aggregate ID |
| `memberNo` | `String` | Member number |
| `nomineeCount` | `Integer` | Number of nominees after save |
| `hasGuarantor` | `Boolean` | Whether a guarantor exists |
| `traceId` | `String` | Correlation trace ID |
| `eventTimestamp` | `LocalDateTime` | Event emission time |

**Event field schema — MemberPhotoUpdatedEvent / MemberSignatureUpdatedEvent:**

| Field | Type | Description |
|-------|------|-------------|
| `memberId` | `String` | Aggregate ID |
| `memberNo` | `String` | Member number |
| `fileReference` | `String` | New file reference |
| `traceId` | `String` | Correlation trace ID |
| `eventTimestamp` | `LocalDateTime` | Event emission time |

**Event field schema — MembershipDocumentUpdatedEvent:**

| Field | Type | Description |
|-------|------|-------------|
| `memberId` | `String` | Aggregate ID |
| `memberNo` | `String` | Member number |
| `membershipFormReference` | `String` | File reference (nullable) |
| `nationalIdReference` | `String` | File reference (nullable) |
| `passportReference` | `String` | File reference (nullable) |
| `drivingLicenseReference` | `String` | File reference (nullable) |
| `photoIdReference` | `String` | File reference (nullable) |
| `surveyFormReference` | `String` | File reference (nullable) |
| `otherFormReference` | `String` | File reference (nullable) |
| `traceId` | `String` | Correlation trace ID |
| `eventTimestamp` | `LocalDateTime` | Event emission time |

**Event field schema — MemberAssetUpdatedEvent:**

| Field | Type | Description |
|-------|------|-------------|
| `memberId` | `String` | Aggregate ID |
| `memberNo` | `String` | Member number |
| `assetCount` | `Integer` | Total active assets after update |
| `traceId` | `String` | Correlation trace ID |
| `eventTimestamp` | `LocalDateTime` | Event emission time |

**Event field schema — MemberProjectOfficerReassignedEvent:**

| Field | Type | Description |
|-------|------|-------------|
| `memberId` | `String` | Aggregate ID |
| `memberNo` | `String` | Member number |
| `newPoId` | `String` | New project officer reference |
| `reassignedDate` | `LocalDate` | Business date of reassignment |
| `traceId` | `String` | Correlation trace ID |
| `eventTimestamp` | `LocalDateTime` | Event emission time |

**Event field schema — MemberAdmissionApprovedEvent / MemberUpdateApprovedEvent:**

| Field | Type | Description |
|-------|------|-------------|
| `memberId` | `String` | Aggregate ID |
| `memberNo` | `String` | Provisioned member number |
| `passbookNo` | `String` | Provisioned passbook number (nullable) |
| `bufferId` | `String` | Originating DCS buffer identifier |
| `dcsApprovalStatus` | `String` | DCS channel response status |
| `traceId` | `String` | Correlation trace ID |
| `eventTimestamp` | `LocalDateTime` | Event emission time |

> 📎 [INFERRED] — required by bits.ddd pattern

---

### DDD-REQ-026 — Failure Event: MemberFailedEvent

When any domain invariant or state-guard check fails, the `Member` aggregate shall construct a `MemberFailedEvent` from `com.bits.ddd.contracts.member.event.*` and throw it wrapped inside `MemberValidationException`.

> **Note:** `MemberValidationException` is a **domain-specific exception** created within the domain application itself — not sourced from any external library. The GlobalExceptionHandler (provided by the implementation layer) will map it to the appropriate HTTP response.

Two failure factory methods on `MemberFailedEvent` shall be used:
- `MemberFailedEvent.validationError(traceId, errors)` — for domain invariant failures
- `MemberFailedEvent.sourceDataError(traceId, errorCode, errors)` — for source-data fetch failures

> 📎 [INFERRED] — required by bits.ddd pattern

---

### DDD-REQ-027 — i18n Message Keys: MemberMessageKey

The Member domain system shall define a local `MemberMessageKey` enum in `enums/` package. Each constant carries a `getKey()` method returning the i18n key string.

Required keys:

| Constant | Key string | Usage |
|---------|-----------|-------|
| `MEMBER` | `"member"` | Entity identifier |
| `NOT_FOUND` | `"member.not.found"` | Aggregate not found by ID |
| `MEMBER_INACTIVE` | `"member.inactive"` | Update/delete attempted on inactive member |
| `MEMBER_CLOSED` | `"member.closed"` | Delete attempted on closed member |
| `MEMBER_DELETE_UNAUTHORIZED` | `"member.delete.unauthorized"` | Delete from wrong office |
| `MEMBER_HAS_LOAN_PROPOSALS` | `"member.has.loan.proposals"` | Delete blocked by loan proposals |
| `MEMBER_HAS_SAVINGS` | `"member.has.savings"` | Delete blocked by savings transactions |
| `MEMBER_NOT_INACTIVE` | `"member.not.inactive"` | Activation attempted on non-inactive member |
| `NO_DATA_TO_SAVE` | `"member.no.data"` | Family update form empty |
| `OFFICE` | `"office"` | Office entity key |
| `BUSINESS_DAY` | `"businessDay"` | Business day key |
| `APP_DATE` | `"applicationDate"` | Application date key |
| `DOB` | `"dateOfBirth"` | Date of birth key |
| `CATEGORY` | `"category"` | Member category key |
| `GROUP` | `"group"` | VO/group key |
| `PO` | `"projectOfficer"` | Project officer key |
| `GENDER` | `"gender"` | Gender key |
| `SAVINGS_PRODUCT` | `"savingsProduct"` | Savings product key |
| `TARGET_AMOUNT` | `"targetAmount"` | Target amount key |
| `NATIONAL_ID` | `"nationalId"` | National ID key |
| `SMART_CARD` | `"smartCardId"` | Smart card key |
| `OTHER_ID` | `"otherIdNo"` | Other identity key |
| `SPOUSE_NID` | `"spouseNationalId"` | Spouse NID key |
| `DEDUPE` | `"deduplication"` | De-duplication key |
| `NOMINEE` | `"nominee"` | Nominee key |
| `GUARDIAN` | `"guardian"` | Guardian key |
| `GUARDIAN_NID` | `"guardianNationalId"` | Guardian NID key |
| `GUARANTOR_NID` | `"guarantorNationalId"` | Guarantor NID key |
| `GUARANTOR_REL` | `"guarantorRelationship"` | Guarantor relationship key |
| `IDENTITY_CONFLICT` | `"identityConflict"` | Member/spouse/guarantor doc cross-check key |
| `BANK` | `"bankInfo"` | Bank information key |
| `PASSBOOK` | `"passbook"` | Passbook key |

> 📎 [INFERRED] — required by bits.ddd pattern

---

## Application Layer

### DDD-REQ-028 — Parameter Object: MemberCreationData

The Member domain system shall define `MemberCreationData` in `domain/param/` as a data-transfer object carrying all data needed to construct the Member aggregate on `create()`.

**Field schema:**

| Field | Type | Description |
|-------|------|-------------|
| `traceId` | `String` | Correlation trace ID (from command) |
| `operatorId` | `String` | Acting operator identifier |
| `businessDate` | `LocalDate` | Branch business date |
| `memberNo` | `String` | Auto-generated member number |
| `countryId` | `String` | Configured country reference |
| `branchInfoId` | `String` | Branch office reference |
| `projectInfoId` | `String` | Project reference |
| `groupInfoId` | `String` | Group reference (nullable) |
| `assignedPoId` | `String` | Project officer reference (nullable) |
| `memberClassificationId` | `String` | Category reference |
| `applicationDate` | `LocalDate` | Application date |
| `fName` | `String` | First name (nullable) |
| `mName` | `String` | Middle name (nullable) |
| `lName` | `String` | Last name (nullable) |
| `memberCustomField` | `String` | Custom field (nullable) |
| `tinNumber` | `String` | Tax ID (nullable) |
| `referredBy` | `String` | Referrer (nullable) |
| `passbookNo` | `String` | Passbook number (nullable) |
| `bankId` | `String` | Bank (nullable) |
| `bankBranchId` | `String` | Bank branch (nullable) |
| `bankAccountNumber` | `String` | Bank account (nullable) |
| `routingNumber` | `String` | Routing number (nullable) |
| `academicQualificationId` | `String` | Qualification (nullable) |
| `personalInfo` | `PersonalInfo` | Personal details |
| `contactInfo` | `ContactInfo` | Contact and address details |
| `nominees` | `List<NomineeInfo>` | Nominees (nullable) |
| `bufferId` | `String` | DCS buffer ID (nullable; set for admission flow) |
| `apiDataSourceId` | `Integer` | Data source flag (nullable) |
| `physicalOfficeInfo` | `PhysicalOfficeInfo` | Branch office snapshot |
| `projectInfo` | `ProjectInfo` | Project snapshot |
| `projectPolicyInfo` | `ProjectPolicyInfo` | Project policy snapshot |
| `groupInfo` | `GroupInfo` | Group snapshot (nullable) |
| `employeeCoreInfo` | `EmployeeCoreInfo` | Field officer snapshot (nullable) |
| `memberClassification` | `MemberClassification` | Category snapshot |
| `savingsProduct` | `SavingsProduct` | Savings product snapshot |
| `savingsProductPolicy` | `SavingsProductPolicy` | Savings product policy snapshot |
| `relationships` | `List<Relationship>` | Valid relationship snapshots |
| `deduplicationResult` | `DeduplicationResult` | Pre-fetched de-duplication result |

> 📎 [INFERRED] — required by bits.ddd pattern

---

### DDD-REQ-029 — Parameter Object: MemberUpdateData

The Member domain system shall define `MemberUpdateData` in `domain/param/`.

**Field schema (nullable fields allow partial update):**

| Field | Type | Description |
|-------|------|-------------|
| `traceId` | `String` | Correlation trace ID |
| `operatorId` | `String` | Acting operator |
| `businessDate` | `LocalDate` | Branch business date |
| `fName` | `String` | First name (nullable) |
| `mName` | `String` | Middle name (nullable) |
| `lName` | `String` | Last name (nullable) |
| `memberCustomField` | `String` | Custom field (nullable) |
| `tinNumber` | `String` | Tax ID (nullable) |
| `memberClassificationId` | `String` | New category (nullable) |
| `passbookNo` | `String` | New passbook (nullable) |
| `bankId` | `String` | Bank (nullable) |
| `bankBranchId` | `String` | Bank branch (nullable) |
| `bankAccountNumber` | `String` | Bank account (nullable) |
| `routingNumber` | `String` | Routing number (nullable) |
| `academicQualificationId` | `String` | Qualification (nullable) |
| `personalInfo` | `PersonalInfo` | Personal details (nullable; merge on non-null) |
| `contactInfo` | `ContactInfo` | Contact details (nullable; merge) |
| `nominees` | `List<NomineeInfo>` | Nominees (nullable) |
| `isUpdate` | `Boolean` | true (used in spec context) |
| `savingsProductChanged` | `Boolean` | Whether savings product changed |
| `hasSavingsTransactions` | `Boolean` | Pre-checked by handler |
| `physicalOfficeInfo` | `PhysicalOfficeInfo` | Branch office snapshot |
| `projectInfo` | `ProjectInfo` | Project snapshot |
| `projectPolicyInfo` | `ProjectPolicyInfo` | Project policy snapshot |
| `groupInfo` | `GroupInfo` | Group snapshot (nullable) |
| `employeeCoreInfo` | `EmployeeCoreInfo` | Field officer snapshot (nullable) |
| `memberClassification` | `MemberClassification` | Category snapshot (nullable) |
| `savingsProduct` | `SavingsProduct` | Savings product snapshot (nullable) |
| `savingsProductPolicy` | `SavingsProductPolicy` | Savings product policy snapshot (nullable) |
| `relationships` | `List<Relationship>` | Relationship snapshots (nullable) |
| `deduplicationResult` | `DeduplicationResult` | De-duplication result (nullable) |

> 📎 [INFERRED] — required by bits.ddd pattern

---

### DDD-REQ-030 — Parameter Object: MemberDeletionData

| Field | Type | Description |
|-------|------|-------------|
| `traceId` | `String` | Correlation trace ID |
| `operatorId` | `String` | Acting operator |
| `operatorBranchId` | `String` | Operator's branch office reference |
| `hasLoanProposals` | `Boolean` | Pre-checked by handler via loan domain |
| `hasSavingsTransactions` | `Boolean` | Pre-checked by handler via savings domain |

> 📎 [INFERRED] — required by bits.ddd pattern

---

### DDD-REQ-031 — Parameter Object: MemberAdmissionApprovalData

Used by `ApproveMemberAdmissionCommandHandler` — same structure as `MemberCreationData` plus DCS-specific fields.

| Field | Type | Description |
|-------|------|-------------|
| All fields from `MemberCreationData` | — | — |
| `bufferRecordType` | `String` | "NEW_MEMBER" or "MEMBER_UPDATE" (from DCS buffer) |
| `dcsChannelApprovalEndpoint` | `String` | DCS channel callback endpoint |

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Module: Member Admission Approval — New-Admission Approval Flow"

---

### DDD-REQ-032 — Parameter Object: MemberUpdateApprovalData

Used by `ApproveMemberUpdateCommandHandler`.

| Field | Type | Description |
|-------|------|-------------|
| All fields from `MemberUpdateData` | — | — |
| `bufferId` | `String` | DCS buffer record identifier |
| `dcsChannelApprovalEndpoint` | `String` | DCS channel callback endpoint |

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Module: Member Admission Approval — Member-Update Approval Flow"

---

### DDD-REQ-033 — Parameter Object: MemberActivationData

| Field | Type | Description |
|-------|------|-------------|
| `traceId` | `String` | Correlation trace ID |
| `operatorId` | `String` | Acting operator |
| `businessDate` | `LocalDate` | Relevant office's business date |
| `memberIds` | `List<String>` | IDs of members to activate |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Activation"

---

### DDD-REQ-034 — Parameter Object: MemberFamilySaveData

| Field | Type | Description |
|-------|------|-------------|
| `traceId` | `String` | Correlation trace ID |
| `operatorId` | `String` | Acting operator |
| `nominees` | `List<NomineeInfo>` | Nominees to save |
| `guardianInfo` | `GuardianInfo` | Guardian (nullable) |
| `guarantorInfo` | `GuarantorInfo` | Guarantor (nullable) |
| `familyInfo` | `FamilyInfo` | Family composition |
| `relationships` | `List<Relationship>` | Valid relationship snapshots |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Family Information"

---

### DDD-REQ-035 — Parameter Object: MemberFamilyUpdateData

| Field | Type | Description |
|-------|------|-------------|
| `traceId` | `String` | Correlation trace ID |
| `operatorId` | `String` | Acting operator |
| `familyInfo` | `FamilyInfo` | Family composition (nullable) |
| `otherOrgLoans` | `List<OtherOrganizationLoan>` | Other-organisation loans |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Family Information"

---

### DDD-REQ-036 — Parameter Object: MemberPhotoUpdateData

| Field | Type | Description |
|-------|------|-------------|
| `traceId` | `String` | Correlation trace ID |
| `operatorId` | `String` | Acting operator |
| `photoReference` | `String` | New photo file reference |
| `supersededPhotoReference` | `String` | Prior photo reference to delete (nullable) |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Photo"

---

### DDD-REQ-037 — Parameter Object: MemberSignatureUpdateData

| Field | Type | Description |
|-------|------|-------------|
| `traceId` | `String` | Correlation trace ID |
| `operatorId` | `String` | Acting operator |
| `signatureReference` | `String` | New signature file reference |
| `supersededSignatureReference` | `String` | Prior signature reference to delete (nullable) |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Signature"

---

### DDD-REQ-038 — Parameter Object: MembershipDocumentUpdateData

| Field | Type | Description |
|-------|------|-------------|
| `traceId` | `String` | Correlation trace ID |
| `operatorId` | `String` | Acting operator |
| `membershipFormReference` | `String` | File reference (nullable) |
| `nationalIdReference` | `String` | File reference (nullable) |
| `passportReference` | `String` | File reference (nullable) |
| `drivingLicenseReference` | `String` | File reference (nullable) |
| `photoIdReference` | `String` | File reference (nullable) |
| `surveyFormReference` | `String` | File reference (nullable) |
| `otherFormReference` | `String` | File reference (nullable) |
| `supersededReferences` | `List<String>` | Prior file references to delete |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Membership Documents"

---

### DDD-REQ-039 — Parameter Object: MemberAssetUpdateData

| Field | Type | Description |
|-------|------|-------------|
| `traceId` | `String` | Correlation trace ID |
| `operatorId` | `String` | Acting operator |
| `assets` | `List<HouseholdAsset>` | Assets to add or update |
| `deletedAssetIds` | `List<String>` | Asset IDs to remove |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Asset Information"

---

### DDD-REQ-040 — Parameter Object: MemberProjectOfficerReassignData

| Field | Type | Description |
|-------|------|-------------|
| `traceId` | `String` | Correlation trace ID |
| `operatorId` | `String` | Acting operator |
| `newPoId` | `String` | New project officer reference |
| `businessDate` | `LocalDate` | Branch business date |
| `memberIds` | `List<String>` | IDs of members to reassign |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Project-Officer Reassignment"

---

### DDD-REQ-041 — Source Data DTO: MemberSourceData

The Member domain system shall define `MemberSourceData` in `application/dto/` holding optional snapshots for each SOURCE_DATA entity. Fields are nullable; the source-data service populates only the entities included in the request map.

| Field | Type | Populated when |
|-------|------|----------------|
| `physicalOfficeInfo` | `PhysicalOfficeInfo` | EntityType `"PHYSICAL_OFFICE"` requested |
| `projectInfo` | `ProjectInfo` | EntityType `"PROJECT"` requested |
| `projectPolicyInfo` | `ProjectPolicyInfo` | EntityType `"PROJECT_POLICY"` requested |
| `groupInfo` | `GroupInfo` | EntityType `"GROUP"` requested |
| `employeeCoreInfo` | `EmployeeCoreInfo` | EntityType `"EMPLOYEE"` requested |
| `memberClassification` | `MemberClassification` | EntityType `"MEMBER_CLASSIFICATION"` requested |
| `savingsProduct` | `SavingsProduct` | EntityType `"SAVINGS_PRODUCT"` requested |
| `savingsProductPolicy` | `SavingsProductPolicy` | EntityType `"SAVINGS_PRODUCT_POLICY"` requested |
| `relationships` | `List<Relationship>` | EntityType `"RELATIONSHIPS"` requested |
| `country` | `Country` | EntityType `"COUNTRY"` requested |
| `memberStatus` | `MemberStatus` | EntityType `"MEMBER_STATUS"` requested |
| `occupation` | `Occupation` | EntityType `"OCCUPATION"` requested |
| `savingsAccount` | `SavingsAccount` | EntityType `"SAVINGS_ACCOUNT"` requested |
| `thanas` | `List<Thana>` | EntityType `"THANA"` requested |

> 📎 [INFERRED] — required by bits.ddd pattern

---

### DDD-REQ-042 — Source Data Request Map: MemberSourceDataRequest

The Member domain system shall define `MemberSourceDataRequest` in `application/service/` with static factory methods:

**Create command map — always includes:**

| EntityType key | Fetch ID source |
|---------------|-----------------|
| `EntityType.PHYSICAL_OFFICE` | `command.getBranchInfoId()` |
| `EntityType.PROJECT` | `command.getProjectInfoId()` |
| `EntityType.PROJECT_POLICY` | `command.getProjectInfoId()` (policy fetched by project ID) |
| `EntityType.MEMBER_CLASSIFICATION` | `command.getMemberClassificationId()` |
| `EntityType.SAVINGS_PRODUCT` | `command.getSavingsProductId()` |
| `EntityType.SAVINGS_PRODUCT_POLICY` | `command.getSavingsProductId()` |
| `EntityType.RELATIONSHIPS` | `"ALL"` (fetches all active relationships) |
| `EntityType.COUNTRY` | configured constant `"BD"` |

**Create command map — conditionally includes:**

| EntityType key | Fetch ID source | Condition |
|---------------|-----------------|-----------|
| `EntityType.GROUP` | `command.getGroupInfoId()` | only when non-null |
| `EntityType.EMPLOYEE` | `command.getAssignedPoId()` | only when non-null |

**Update command map — conditionally includes:**

| EntityType key | Fetch ID source | Condition |
|---------------|-----------------|-----------|
| `EntityType.PHYSICAL_OFFICE` | `command.getBranchInfoId()` | always |
| `EntityType.PROJECT` | `command.getProjectInfoId()` | always |
| `EntityType.PROJECT_POLICY` | `command.getProjectInfoId()` | always |
| `EntityType.MEMBER_CLASSIFICATION` | `command.getMemberClassificationId()` | when non-null |
| `EntityType.SAVINGS_PRODUCT` | `command.getSavingsProductId()` | when non-null |
| `EntityType.SAVINGS_PRODUCT_POLICY` | `command.getSavingsProductId()` | when non-null |
| `EntityType.GROUP` | `command.getGroupInfoId()` | when non-null |
| `EntityType.EMPLOYEE` | `command.getAssignedPoId()` | when non-null |
| `EntityType.RELATIONSHIPS` | `"ALL"` | when nominees or guarantor updated |

> 📎 [INFERRED] — required by bits.ddd pattern

---

### DDD-REQ-043 — Source Data Service: MemberSourceDataService

The Member domain system shall implement `MemberSourceDataService` implementing `SourceDataService<MemberSourceData>` in `application/service/`. The service shall:

1. Read `trace_id` from `MDC.get("trace_id")`, falling back to `"no-trace-id"`.
2. Fan out fetch calls concurrently using `CompletableFuture` per entity in the request map.
3. Delegate each fetch to `MemberSourceDataFactory`.
4. On any fetch error, collect errors and throw `MemberFailedEvent.sourceDataError(...)` wrapped in `MemberValidationException`.
5. Be annotated with `@Observed` for Micrometer tracing.

**Behaviour — getSourceData(entityTypeIdMap):** [PSEUDOCODE]

```pseudocode
getSourceData(entityTypeIdMap):
  traceId = MDC.get("trace_id") ?? "no-trace-id"
  futures = []
  FOR EACH (entityType, id) IN entityTypeIdMap DO
    future = CompletableFuture.supplyAsync(() -> factory.fetch(entityType, id))
    futures.add(future)
  END FOR
  CompletableFuture.allOf(futures).join()
  errors = []
  FOR EACH future IN futures DO
    IF future failed THEN errors.add(future.cause.message) END IF
  END FOR
  IF errors is not empty THEN
    failureEvent = MemberFailedEvent.sourceDataError(traceId, SOURCE_DATA_ERROR, errors)
    THROW MemberValidationException(failureEvent)
  END IF
  RETURN assembleSourceData(futures.results)
```

> 📎 [INFERRED] — required by bits.ddd pattern

---

### DDD-REQ-044 — Source Data Factory: MemberSourceDataFactory

The Member domain system shall implement `MemberSourceDataFactory` in `application/service/`. In a `@PostConstruct` method it shall register a snapshot creator per `EntityType` key, each delegating to its corresponding infrastructure snapshot repository. Each creator converts the result using `toModel()` + `JsonUtil.convert(...)`.

> 📎 [INFERRED] — required by bits.ddd pattern

---

### DDD-REQ-045 — Source-Data Event Listeners (EVENT Sourcing Pipeline)

The Member domain system shall implement event-driven source-data capture. For each SOURCE_DATA entity, a dedicated `@RabbitListener` captures creation, update, and deletion events from the owning domain, persisting local snapshots in MongoDB.

---

**DDD-REQ-045a — PhysicalOffice Source-Data Listener**

The system shall implement `PhysicalOfficeSourceDataListener` in `application/sourcedata/`, annotated `@Service`.

External event DTO: `PhysicalOfficeCreatedEvent / PhysicalOfficeUpdatedEvent / PhysicalOfficeDeletedEvent`

| Field | Type | Description |
|-------|------|-------------|
| `officeId` | `String` | PhysicalOffice identifier |
| `officeCode` | `String` | Office code |
| `officeName` | `String` | Office name |
| `officeType` | `String` | "BRANCH" or "HEAD_OFFICE" |
| `businessDayStatus` | `String` | "OPEN" or "CLOSED" |
| `businessDate` | `LocalDate` | Current business date |
| `branchInfoId` | `String` | Parent branch reference (nullable) |
| `isActive` | `Boolean` | Active status |
| `eventTimestamp` | `LocalDateTime` | Event time |

**Snapshot schema — PhysicalOfficeInfoDocument** (persisted to `physical_office_info_snapshots`):

| Field | Java Type | @Field | Description |
|-------|-----------|--------|-------------|
| `id` | `String` | `_id` | Office ID |
| `officeCode` | `String` | `office_code` | Office code (unique index) |
| `officeName` | `String` | `office_name` | Office name |
| `officeType` | `String` | `office_type` | "BRANCH" or "HEAD_OFFICE" |
| `businessDayStatus` | `String` | `business_day_status` | "OPEN" or "CLOSED" |
| `businessDate` | `LocalDate` | `business_date` | Current business date |
| `isActive` | `Boolean` | `is_active` | Active flag |
| `lastEventTimestamp` | `LocalDateTime` | `last_event_timestamp` | Last received event time |

**Listener implementation pseudocode:**

```pseudocode
@RabbitListener(queues = RabbitMQConstants.PHYSICAL_OFFICE_CREATED_QUEUE)
onPhysicalOfficeCreated(PhysicalOfficeCreatedEvent event):
  doc = new PhysicalOfficeInfoDocument()
  doc.id = event.officeId; doc.officeCode = event.officeCode
  doc.officeName = event.officeName; doc.officeType = event.officeType
  doc.businessDayStatus = event.businessDayStatus; doc.businessDate = event.businessDate
  doc.isActive = true; doc.lastEventTimestamp = event.eventTimestamp
  physicalOfficeInfoRepository.save(doc)

@RabbitListener(queues = RabbitMQConstants.PHYSICAL_OFFICE_UPDATED_QUEUE)
onPhysicalOfficeUpdated(PhysicalOfficeUpdatedEvent event):
  doc = physicalOfficeInfoRepository.findById(event.officeId).orElse(new PhysicalOfficeInfoDocument())
  doc.officeCode = event.officeCode; doc.officeName = event.officeName
  doc.officeType = event.officeType; doc.businessDayStatus = event.businessDayStatus
  doc.businessDate = event.businessDate; doc.lastEventTimestamp = event.eventTimestamp
  physicalOfficeInfoRepository.save(doc)

@RabbitListener(queues = RabbitMQConstants.PHYSICAL_OFFICE_DELETED_QUEUE)
onPhysicalOfficeDeleted(PhysicalOfficeDeletedEvent event):
  doc = physicalOfficeInfoRepository.findById(event.officeId).orElse(new PhysicalOfficeInfoDocument())
  doc.isActive = false; doc.lastEventTimestamp = event.eventTimestamp
  physicalOfficeInfoRepository.save(doc)
```

> 📎 [INFERRED] — required by EVENT sourcing pattern (Gate 2b, Gate 2c)

---

**DDD-REQ-045b — Project Source-Data Listener**

The system shall implement `ProjectSourceDataListener` capturing `ProjectInfo` events.

External event DTO fields: `projectId`, `projectCode`, `projectName`, `projectStatus`, `associationType (GROUP|MEMBER|NA)`, `hasMembershipFee`, `feeAmount`, `hasPassbook`, `passbookPrice`, `collectionFrequency`, `isActive`, `eventTimestamp`

Snapshot collection: `project_info_snapshots`

| Field | Java Type | @Field | Description |
|-------|-----------|--------|-------------|
| `id` | `String` | `_id` | Project ID |
| `projectCode` | `String` | `project_code` | Project code |
| `projectName` | `String` | `project_name` | Project name |
| `projectStatus` | `String` | `project_status` | Project lifecycle status |
| `associationType` | `String` | `association_type` | GROUP, MEMBER, or NA |
| `isActive` | `Boolean` | `is_active` | Active flag |
| `lastEventTimestamp` | `LocalDateTime` | `last_event_timestamp` | Last event time |

Listener pattern: same as `PhysicalOfficeSourceDataListener` — `@RabbitListener` for created/updated/deleted queues, upsert on created/updated, set `isActive=false` on deleted.

> 📎 [INFERRED] — required by EVENT sourcing pattern

---

**DDD-REQ-045c — ProjectPolicy Source-Data Listener**

Snapshot collection: `project_policy_info_snapshots`

| Field | Java Type | @Field | Description |
|-------|-----------|--------|-------------|
| `id` | `String` | `_id` | Policy ID |
| `projectInfoId` | `String` | `project_info_id` | Owning project reference |
| `associationType` | `String` | `association_type` | GROUP / MEMBER / NA |
| `hasMembershipFee` | `Boolean` | `has_membership_fee` | Whether a membership fee is charged |
| `feeAmount` | `BigDecimal` | `fee_amount` | Membership fee amount |
| `hasPassbook` | `Boolean` | `has_passbook` | Whether passbooks are used |
| `passbookPrice` | `BigDecimal` | `passbook_price` | Passbook price |
| `collectionFrequency` | `String` | `collection_frequency` | Savings collection frequency |
| `isActive` | `Boolean` | `is_active` | Active flag |
| `lastEventTimestamp` | `LocalDateTime` | `last_event_timestamp` | Last event time |

> 📎 [INFERRED] — required by EVENT sourcing pattern

---

**DDD-REQ-045d — Group Source-Data Listener**

Snapshot collection: `group_info_snapshots`

| Field | Java Type | @Field | Description |
|-------|-----------|--------|-------------|
| `id` | `String` | `_id` | Group ID |
| `groupCode` | `String` | `group_code` | Group code |
| `groupName` | `String` | `group_name` | Group name |
| `applicableGender` | `String` | `applicable_gender` | "MALE", "FEMALE", or "BOTH" (3 = both) |
| `groupStatus` | `String` | `group_status` | "ACTIVE" or "INACTIVE" |
| `branchInfoId` | `String` | `branch_info_id` | Owning branch office |
| `projectInfoId` | `String` | `project_info_id` | Owning project |
| `assignedPoId` | `String` | `assigned_po_id` | Assigned field officer (nullable) |
| `lastPoAssignedDate` | `LocalDate` | `last_po_assigned_date` | Date PO was assigned |
| `isActive` | `Boolean` | `is_active` | Active flag |
| `lastEventTimestamp` | `LocalDateTime` | `last_event_timestamp` | Last event time |

> 📎 [INFERRED] — required by EVENT sourcing pattern

---

**DDD-REQ-045e — Employee Source-Data Listener**

Snapshot collection: `employee_core_info_snapshots`

| Field | Java Type | @Field | Description |
|-------|-----------|--------|-------------|
| `id` | `String` | `_id` | Employee ID |
| `employeeCode` | `String` | `employee_code` | Employee code |
| `employeeName` | `String` | `employee_name` | Employee full name |
| `branchInfoId` | `String` | `branch_info_id` | Assigned branch |
| `projectInfoId` | `String` | `project_info_id` | Assigned project |
| `isActive` | `Boolean` | `is_active` | Active flag |
| `lastEventTimestamp` | `LocalDateTime` | `last_event_timestamp` | Last event time |

> 📎 [INFERRED] — required by EVENT sourcing pattern

---

**DDD-REQ-045f — MemberClassification Source-Data Listener**

Snapshot collection: `member_classification_snapshots`

| Field | Java Type | @Field | Description |
|-------|-----------|--------|-------------|
| `id` | `String` | `_id` | Classification ID |
| `categoryName` | `String` | `category_name` | Category name |
| `ageFrom` | `Integer` | `age_from` | Minimum allowed age |
| `ageTo` | `Integer` | `age_to` | Maximum allowed age |
| `isAllowedLoan` | `Boolean` | `is_allowed_loan` | Whether loans allowed |
| `hasSavings` | `Boolean` | `has_savings` | Whether savings apply |
| `isDisallowMemberFees` | `Boolean` | `is_disallow_member_fees` | Whether membership fees disallowed |
| `domainStatusId` | `Integer` | `domain_status_id` | Active/inactive |
| `isActive` | `Boolean` | `is_active` | Active flag |
| `lastEventTimestamp` | `LocalDateTime` | `last_event_timestamp` | Last event time |

> 📎 [INFERRED] — required by EVENT sourcing pattern

---

**DDD-REQ-045g — SavingsProduct Source-Data Listener**

Snapshot collection: `savings_product_snapshots`

| Field | Java Type | @Field | Description |
|-------|-----------|--------|-------------|
| `id` | `String` | `_id` | Product ID |
| `productCode` | `String` | `product_code` | Product code |
| `productName` | `String` | `product_name` | Product name (max 150) |
| `productType` | `String` | `product_type` | Savings product type enum |
| `collectionFrequency` | `String` | `collection_frequency` | Collection frequency for availability check |
| `domainStatusId` | `Integer` | `domain_status_id` | Active/inactive |
| `isActive` | `Boolean` | `is_active` | Active flag |
| `lastEventTimestamp` | `LocalDateTime` | `last_event_timestamp` | Last event time |

> 📎 [INFERRED] — required by EVENT sourcing pattern

---

**DDD-REQ-045h — SavingsProductPolicy Source-Data Listener**

Snapshot collection: `savings_product_policy_snapshots`

| Field | Java Type | @Field | Description |
|-------|-----------|--------|-------------|
| `id` | `String` | `_id` | Policy ID |
| `savingsProductId` | `String` | `savings_product_id` | Owning product |
| `minDepositAmount` | `BigDecimal` | `min_deposit_amount` | Minimum deposit / installment amount |
| `minimumBalance` | `BigDecimal` | `minimum_balance` | Minimum balance |
| `calculationFrequency` | `String` | `calculation_frequency` | Frequency enum |
| `isActive` | `Boolean` | `is_active` | Active flag |
| `lastEventTimestamp` | `LocalDateTime` | `last_event_timestamp` | Last event time |

> 📎 [INFERRED] — required by EVENT sourcing pattern

---

**DDD-REQ-045i — Relationship Source-Data Listener**

Snapshot collection: `relationship_snapshots`

| Field | Java Type | @Field | Description |
|-------|-----------|--------|-------------|
| `id` | `String` | `_id` | Relationship ID |
| `name` | `String` | `name` | Relationship name (unique) |
| `isRelative` | `Boolean` | `is_relative` | Whether a relative |
| `isSpouseRelationship` | `Boolean` | `is_spouse_relationship` | True when relationship ID = "20" |
| `statusId` | `Integer` | `status_id` | Active/inactive |
| `isActive` | `Boolean` | `is_active` | Active flag |
| `lastEventTimestamp` | `LocalDateTime` | `last_event_timestamp` | Last event time |

> 📎 [INFERRED] — required by EVENT sourcing pattern

---

**DDD-REQ-045j — Country Source-Data Listener**

Snapshot collection: `country_snapshots`

| Field | Java Type | @Field | Description |
|-------|-----------|--------|-------------|
| `id` | `String` | `_id` | Country ID |
| `name` | `String` | `name` | Country name |
| `code` | `String` | `code` | Country code (e.g. "BD") |
| `timeZone` | `String` | `time_zone` | Time zone |
| `isActive` | `Boolean` | `is_active` | Active flag |
| `lastEventTimestamp` | `LocalDateTime` | `last_event_timestamp` | Last event time |

> 📎 [INFERRED] — required by EVENT sourcing pattern

---

**DDD-REQ-045k — MemberStatus Source-Data Listener**

Snapshot collection: `member_status_snapshots`

| Field | Java Type | @Field | Description |
|-------|-----------|--------|-------------|
| `id` | `String` | `_id` | Status ID (e.g. "1" = Active, "0" = Zero) |
| `statusCode` | `String` | `status_code` | Status code |
| `statusName` | `String` | `status_name` | Status name |
| `isActive` | `Boolean` | `is_active` | Active flag |
| `lastEventTimestamp` | `LocalDateTime` | `last_event_timestamp` | Last event time |

> 📎 [INFERRED] — required by EVENT sourcing pattern

---

**DDD-REQ-045l — Occupation Source-Data Listener**

Snapshot collection: `occupation_snapshots`

| Field | Java Type | @Field | Description |
|-------|-----------|--------|-------------|
| `id` | `String` | `_id` | Occupation ID |
| `occupationName` | `String` | `occupation_name` | Occupation name |
| `occupationCode` | `String` | `occupation_code` | Code |
| `isActive` | `Boolean` | `is_active` | Active flag |
| `lastEventTimestamp` | `LocalDateTime` | `last_event_timestamp` | Last event time |

> 📎 [INFERRED] — required by EVENT sourcing pattern

---

**DDD-REQ-045m — SavingsAccount Source-Data Listener**

Snapshot collection: `savings_account_snapshots`

| Field | Java Type | @Field | Description |
|-------|-----------|--------|-------------|
| `id` | `String` | `_id` | Savings account ID |
| `memberId` | `String` | `member_id` | Owning member ID |
| `savingsProductId` | `String` | `savings_product_id` | Savings product reference |
| `targetAmount` | `BigDecimal` | `target_amount` | Current target amount |
| `hasTransactions` | `Boolean` | `has_transactions` | Whether any transactions exist |
| `branchInfoId` | `String` | `branch_info_id` | Branch reference |
| `isActive` | `Boolean` | `is_active` | Active flag |
| `lastEventTimestamp` | `LocalDateTime` | `last_event_timestamp` | Last event time |

> 📎 [INFERRED] — required by EVENT sourcing pattern

---

**DDD-REQ-045n — Thana Source-Data Listener**

Snapshot collection: `thana_snapshots`

| Field | Java Type | @Field | Description |
|-------|-----------|--------|-------------|
| `id` | `String` | `_id` | Thana (sub-district) ID |
| `thanaCode` | `String` | `thana_code` | Thana code |
| `thanaName` | `String` | `thana_name` | Thana name |
| `districtId` | `String` | `district_id` | Parent district reference |
| `countryId` | `String` | `country_id` | Country reference |
| `isActive` | `Boolean` | `is_active` | Active flag |
| `lastEventTimestamp` | `LocalDateTime` | `last_event_timestamp` | Last event time |

> 📎 [INFERRED] — required by EVENT sourcing pattern

---

### DDD-REQ-046 — Command Handler: CreateMemberCommandHandler

The Member domain system shall implement `CreateMemberCommandHandler` in `application/commandhandler/`, annotated `@RegisterCommandHandler @Service`, implementing `CommandHandler<CreateMemberCommand>`.

**Responsibilities (in order):**
1. Read `trace_id` from `MDC`; call `MDC.put("trace_id", traceId)`.
2. Check concurrency locks: identity lock (on national ID + smart card + other ID combined) and branch-project-group lock. Reject with "Member Creation process is on going. Please wait and check after few minutes." if either lock is held.
3. Pre-call external de-duplication service for identity documents.
4. Fetch source data: `sourceDataService.getSourceData(MemberSourceDataRequest.getCreateMap(command))`.
5. Auto-generate member number.
6. Map to param object: `MemberDataMapper.toCreationData(command, sourceData, deduplicationResult, businessDate, memberNo)`.
7. Create aggregate: `Member.create(creationData)`.
8. Persist: `persistenceService.persist(member)`.
9. Publish events: `messagingProcessor.publish(member.getEvents())`.
10. Release both concurrency locks.

**Injected dependencies:**
- `@PersistDomain DomainPersistenceService<Member, String>`
- `SourceDataService<MemberSourceData>`
- `MessageProcessor`
- `MemberConcurrencyLockService` (manages identity + branch-project-group locks)
- `DeduplicationService` (external de-duplication HTTP client)

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Registration"
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Cross-Cutting Requirements — Operational Cross-Cuts"

---

### DDD-REQ-047 — Command Handler: ApproveMemberAdmissionCommandHandler

**Responsibilities (in order):**
1. Read `trace_id` from `MDC`.
2. Check and acquire both concurrency locks (same as CreateMember).
3. Call DCS member channel to retrieve the buffer record for the supplied `bufferId`.
4. Optionally attach passbook number from request.
5. Pre-call de-duplication service.
6. Fetch source data: `sourceDataService.getSourceData(MemberSourceDataRequest.getCreateMap(command))`.
7. Auto-generate member number.
8. Map to `MemberAdmissionApprovalData`.
9. Call AR method: `Member.approveMemberAdmission(admissionData)`. ← **domain decision + `addEvent(MemberAdmissionApprovedEvent)` happen INSIDE this method**
10. Persist: `persistenceService.persist(member)`.
11. Post approval to DCS channel: carry provisioned member ID, member number, passbook number, and success message "Member successfully saved".
12. If DCS post fails: report failure to operator with DCS channel error message.
13. Publish domain events: `messagingProcessor.publish(member.getEvents())`. ← **orchestration only; handler NEVER calls `addEvent(...)`**
14. Release both concurrency locks.

On validation failure:
- Post failure reason to DCS channel as update-messages notification with zero approved-transaction ID.
- Report failure to operator with validation failure message.

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Module: Member Admission Approval — New-Admission Approval Flow"

---

### DDD-REQ-048 — Command Handler: UpdateMemberCommandHandler

**Responsibilities (in order):**
0. Load aggregate: `queryService.fetchByIdOrHandleFailure(command.getMemberId(), traceId)`.
1. Pre-call de-duplication service (when identity documents changed).
2. Pre-check: outstanding loan balance and pending loan proposal (when memberClassificationId changed) — via loan domain.
3. Pre-check: savings account transactions (when savings product changed) — via savings domain.
4. Fetch source data: `sourceDataService.getSourceData(MemberSourceDataRequest.getUpdateMap(command))`.
5. Map to `MemberUpdateData`.
6. Update aggregate: `member.update(updateData)`.
7. Persist: `persistenceService.persist(member)`.
8. Publish events: `messagingProcessor.publish(member.getEvents())`.

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Profile Update"

---

### DDD-REQ-049 — Command Handler: ApproveMemberUpdateCommandHandler

**Responsibilities (in order):**
0. Load aggregate: `queryService.fetchByIdOrHandleFailure(command.getMemberId(), traceId)`.
1. Call DCS channel to retrieve buffer record.
2. Pre-call de-duplication service (when identity changed).
3. Fetch source data.
4. Map to `MemberUpdateApprovalData`.
5. Call AR method: `member.approveMemberUpdate(updateApprovalData)`. ← **domain decision + `addEvent(MemberUpdateApprovedEvent)` happen INSIDE this method**
6. Persist: `persistenceService.persist(member)`.
7. Post approval to DCS channel: carry updated member ID and success message "Member successfully updated".
8. On validation failure: post rejection to DCS channel with "Member Rejected to admit into system".
9. Publish domain events: `messagingProcessor.publish(member.getEvents())`. ← **orchestration only; handler NEVER calls `addEvent(...)`**

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Module: Member Admission Approval — Member-Update Approval Flow"

---

### DDD-REQ-050 — Command Handler: DeleteMemberCommandHandler

**Responsibilities (in order):**
0. Load aggregate: `queryService.fetchByIdOrHandleFailure(command.getMemberId(), traceId)`.
1. Check loan proposals via loan domain → populate `hasLoanProposals` flag.
2. Check savings transactions via savings domain → populate `hasSavingsTransactions` flag.
3. Map to `MemberDeletionData`.
4. Delete aggregate: `member.delete(deletionData)`.
5. Persist: `persistenceService.persist(member)`.
6. Publish events: `messagingProcessor.publish(member.getEvents())`.

**Note:** Async reversal of accounting vouchers is handled by the accounting domain upon receiving `MemberDeletedEvent`.

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Deletion"

---

### DDD-REQ-051 — Command Handler: ActivateMemberCommandHandler

**Responsibilities (in order):**
- Verify branch business day is open (from physicalOfficeInfo snapshot).
- For each member ID in the activation list:
  0. Load aggregate: `queryService.fetchByIdOrHandleFailure(memberId, traceId)`.
  1. Map to `MemberActivationData`.
  2. Activate: `member.activate(activationData)`.
  3. Persist: `persistenceService.persist(member)`.
  4. Publish events: `messagingProcessor.publish(member.getEvents())`.

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Activation"

---

### DDD-REQ-052 — Command Handler: SaveMemberFamilyCommandHandler

**Responsibilities (in order):**
0. Load aggregate: `queryService.fetchByIdOrHandleFailure(command.getMemberId(), traceId)`.
1. Fetch relationships: `sourceDataService.getSourceData(...)`.
2. Map to `MemberFamilySaveData`.
3. Save family: `member.saveFamily(familyData)`.
4. Persist: `persistenceService.persist(member)`.
5. Publish events: `messagingProcessor.publish(member.getEvents())`.

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Family Information"

---

### DDD-REQ-053 — Command Handler: UpdateMemberFamilyCommandHandler

0. Load aggregate.
1. Map to `MemberFamilyUpdateData`.
2. Update family: `member.updateFamily(familyData)`.
3. Persist → Publish.

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Family Information"

---

### DDD-REQ-054 — Command Handler: UpdateMemberPhotoCommandHandler

0. Load aggregate: `queryService.fetchByIdOrHandleFailure(...)`.
1. Map to `MemberPhotoUpdateData`.
2. Update photo: `member.updatePhoto(photoData)`.
3. Persist.
4. Asynchronously: relocate uploaded photo to permanent storage; delete superseded photo.
5. Publish events.

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Photo"

---

### DDD-REQ-055 — Command Handler: UpdateMemberSignatureCommandHandler

0. Load aggregate.
1. Map to `MemberSignatureUpdateData`.
2. Update signature: `member.updateSignature(signatureData)`.
3. Persist.
4. Asynchronously: relocate signature file; delete superseded file.
5. Publish events.

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Signature"

---

### DDD-REQ-056 — Command Handler: UpdateMembershipDocumentCommandHandler

0. Load aggregate.
1. Map to `MembershipDocumentUpdateData`.
2. Update documents: `member.updateMembershipDocument(documentData)`.
3. Persist.
4. Asynchronously: relocate uploaded files to permanent storage under standardised names; delete superseded files.
5. Publish events.

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Membership Documents"

---

### DDD-REQ-057 — Command Handler: UpdateMemberAssetCommandHandler

0. Load aggregate.
1. Map to `MemberAssetUpdateData`.
2. Update assets: `member.updateAssets(assetData)`.
3. Persist → Publish.

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management — Member Asset Information"

---

### DDD-REQ-058 — Command Handler: ReassignProjectOfficerCommandHandler

For each member ID in the reassignment list:
0. Load aggregate: `queryService.fetchByIdOrHandleFailure(memberId, traceId)`.
1. Map to `MemberProjectOfficerReassignData`.
2. Reassign: `member.reassignProjectOfficer(reassignData)`.
3. Persist → Publish.

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Project-Officer Reassignment"

---

### DDD-REQ-059 — Query Service: MemberQueryService

The Member domain system shall implement `MemberQueryService` in `application/service/`. It shall inject `DomainRepository<Member, String>` via `@MongoDomainRepo`.

**Behaviour — fetchByIdOrHandleFailure(id, traceId):** [PSEUDOCODE]

```pseudocode
fetchByIdOrHandleFailure(id, traceId):
  result = domainRepository.findById(id)
  IF result is empty THEN
    errors = Map.of(MessageKey.MEMBER.getKey(),
              LocalizedMessage(key = MessageKey.NOT_FOUND.getKey(), args = [id]))
    failureEvent = MemberFailedEvent.validationError(traceId, errors)
    THROW MemberValidationException(failureEvent)
  END IF
  RETURN result.value
```

> 📎 [INFERRED] — required by bits.ddd pattern

---

### DDD-REQ-060 — Data Mapper: MemberDataMapper

The Member domain system shall implement `MemberDataMapper` in `application/mapper/` as a static utility class. It shall provide:

- `toCreationData(CreateMemberCommand, MemberSourceData, DeduplicationResult, LocalDate, String)` → `MemberCreationData`
- `toAdmissionApprovalData(ApproveMemberAdmissionCommand, MemberSourceData, DeduplicationResult, LocalDate, String)` → `MemberAdmissionApprovalData`
- `toUpdateData(UpdateMemberCommand, MemberSourceData, Member, DeduplicationResult, LocalDate)` → `MemberUpdateData`
- `toUpdateApprovalData(ApproveMemberUpdateCommand, MemberSourceData, Member, DeduplicationResult, LocalDate)` → `MemberUpdateApprovalData`
- `toDeletionData(DeleteMemberCommand, boolean, boolean)` → `MemberDeletionData`
- `toActivationData(ActivateMemberCommand, LocalDate)` → `MemberActivationData`
- `toFamilySaveData(SaveMemberFamilyCommand, List<Relationship>)` → `MemberFamilySaveData`
- `toFamilyUpdateData(UpdateMemberFamilyCommand)` → `MemberFamilyUpdateData`
- `toPhotoUpdateData(UpdateMemberPhotoCommand)` → `MemberPhotoUpdateData`
- `toSignatureUpdateData(UpdateMemberSignatureCommand)` → `MemberSignatureUpdateData`
- `toMembershipDocumentUpdateData(UpdateMembershipDocumentCommand)` → `MembershipDocumentUpdateData`
- `toAssetUpdateData(UpdateMemberAssetCommand)` → `MemberAssetUpdateData`
- `toReassignData(ReassignProjectOfficerCommand, LocalDate)` → `MemberProjectOfficerReassignData`

> 📎 [INFERRED] — required by bits.ddd pattern

---

### DDD-REQ-061 — Command Mapper: MemberCommandMapper

The Member domain system shall implement `MemberCommandMapper` in `application/mapper/` as a static utility class:

- `toCreateCommand(traceId, CreateMemberRequest)` → `CreateMemberCommand`
- `toApproveMemberAdmissionCommand(traceId, ApproveMemberAdmissionRequest)` → `ApproveMemberAdmissionCommand`
- `toUpdateCommand(traceId, id, UpdateMemberRequest)` → `UpdateMemberCommand`
- `toApproveMemberUpdateCommand(traceId, id, ApproveMemberUpdateRequest)` → `ApproveMemberUpdateCommand`
- `toDeleteCommand(traceId, id, operatorBranchId)` → `DeleteMemberCommand`
- `toActivateMemberCommand(traceId, ActivateMemberRequest)` → `ActivateMemberCommand`
- `toSaveMemberFamilyCommand(traceId, id, SaveMemberFamilyRequest)` → `SaveMemberFamilyCommand`
- `toUpdateMemberFamilyCommand(traceId, id, UpdateMemberFamilyRequest)` → `UpdateMemberFamilyCommand`
- `toUpdateMemberPhotoCommand(traceId, id, UpdateMemberPhotoRequest)` → `UpdateMemberPhotoCommand`
- `toUpdateMemberSignatureCommand(traceId, id, UpdateMemberSignatureRequest)` → `UpdateMemberSignatureCommand`
- `toUpdateMembershipDocumentCommand(traceId, id, UpdateMembershipDocumentRequest)` → `UpdateMembershipDocumentCommand`
- `toUpdateMemberAssetCommand(traceId, id, UpdateMemberAssetRequest)` → `UpdateMemberAssetCommand`
- `toReassignProjectOfficerCommand(traceId, ReassignProjectOfficerRequest)` → `ReassignProjectOfficerCommand`

> 📎 [INFERRED] — required by bits.ddd pattern

---

## Presentation Layer

### DDD-REQ-062 — Command: CreateMemberCommand

The Member domain system shall define `CreateMemberCommand` as an immutable Java `record` in `application/command/`.

| Field | Type | Constraint | Description |
|-------|------|-----------|-------------|
| `traceId` | `String` | `@NotBlank` | Correlation trace ID |
| `operatorId` | `String` | `@NotBlank` | Acting operator |
| `branchInfoId` | `String` | `@NotBlank` | Branch office reference |
| `projectInfoId` | `String` | `@NotBlank` | Project reference |
| `groupInfoId` | `String` | `@Nullable` | Group (VO) reference |
| `assignedPoId` | `String` | `@Nullable` | Field officer reference |
| `memberClassificationId` | `String` | `@NotBlank` | Category reference |
| `savingsProductId` | `String` | `@NotBlank` | Savings product reference |
| `targetAmount` | `BigDecimal` | `@NotNull` | Target savings amount |
| `applicationDate` | `LocalDate` | `@NotNull` | Application date |
| `fName` | `String` | `@Nullable` | First name |
| `mName` | `String` | `@Nullable` | Middle name |
| `lName` | `String` | `@Nullable` | Last name |
| `genderId` | `String` | `@NotBlank` | Gender |
| `maritalStatusId` | `String` | `@NotBlank` | Marital status |
| `dateOfBirth` | `LocalDate` | `@Nullable` | Date of birth |
| `occupationId` | `String` | `@Nullable` | Occupation reference |
| `fatherName` | `String` | `@Nullable` | Father name |
| `motherName` | `String` | `@Nullable` | Mother name |
| `spouseName` | `String` | `@Nullable` | Spouse name |
| `nationalId` | `String` | `@Nullable` | National ID |
| `smartCardId` | `String` | `@Nullable` | Smart card |
| `passportNo` | `String` | `@Nullable` | Passport number |
| `drivingLicenseNo` | `String` | `@Nullable` | Driving licence |
| `photoIdNo` | `String` | `@Nullable` | Photo identity |
| `otherIdTypeId` | `Integer` | `@Nullable` | Other ID type |
| `otherIdTypeNo` | `String` | `@Nullable` | Other ID number |
| `contactNo` | `String` | `@NotBlank` | Primary contact |
| `presentAddress` | `String` | `@Nullable` | Present address |
| `presentThanaId` | `String` | `@Nullable` | Present upazila |
| `permanentAddress` | `String` | `@Nullable` | Permanent address |
| `permanentThanaId` | `String` | `@Nullable` | Permanent upazila |
| `passbookNo` | `String` | `@Nullable` | Passbook number |
| `nominees` | `List<NomineeInfo>` | `@Nullable` | Nominees |
| `guarantorInfo` | `GuarantorInfo` | `@Nullable` | Guarantor details |
| `bankId` | `String` | `@Nullable` | Bank |
| `bankBranchId` | `String` | `@Nullable` | Bank branch |
| `bankAccountNumber` | `String` | `@Nullable` | Bank account |
| `routingNumber` | `String` | `@Nullable` | Routing number |
| `tinNumber` | `String` | `@Nullable` | Tax ID (max 12, alphanumeric) |
| `memberCustomField` | `String` | `@Nullable` | Custom field (max 4) |
| `academicQualificationId` | `String` | `@Nullable` | Academic qualification |
| `referredBy` | `String` | `@Nullable` | Referrer |

---

### DDD-REQ-063 — Command: ApproveMemberAdmissionCommand

| Field | Type | Constraint | Description |
|-------|------|-----------|-------------|
| `traceId` | `String` | `@NotBlank` | Correlation trace ID |
| `operatorId` | `String` | `@NotBlank` | Acting operator |
| `bufferId` | `String` | `@NotBlank` | DCS buffer record identifier |
| `passbookNo` | `String` | `@Nullable` | New passbook number (optionally supplied by operator) |
| `branchInfoId` | `String` | `@NotBlank` | Branch reference (from buffer) |

---

### DDD-REQ-064 — Command: UpdateMemberCommand

| Field | Type | Constraint | Description |
|-------|------|-----------|-------------|
| `traceId` | `String` | `@NotBlank` | Correlation trace ID |
| `operatorId` | `String` | `@NotBlank` | Acting operator |
| `memberId` | `String` | `@NotBlank` | Member identifier |
| `branchInfoId` | `String` | `@NotBlank` | Branch reference |
| `projectInfoId` | `String` | `@NotBlank` | Project reference |
| `fName` | `String` | `@Nullable` | First name |
| `mName` | `String` | `@Nullable` | Middle name |
| `lName` | `String` | `@Nullable` | Last name |
| `genderId` | `String` | `@Nullable` | Gender (when updating) |
| `maritalStatusId` | `String` | `@Nullable` | Marital status (when updating) |
| `dateOfBirth` | `LocalDate` | `@Nullable` | Date of birth (when updating) |
| `occupationId` | `String` | `@Nullable` | Occupation |
| `fatherName` | `String` | `@Nullable` | Father name |
| `motherName` | `String` | `@Nullable` | Mother name |
| `spouseName` | `String` | `@Nullable` | Spouse name |
| `nationalId` | `String` | `@Nullable` | National ID (when changing) |
| `smartCardId` | `String` | `@Nullable` | Smart card (when changing) |
| `passportNo` | `String` | `@Nullable` | Passport (when changing) |
| `drivingLicenseNo` | `String` | `@Nullable` | Driving licence |
| `photoIdNo` | `String` | `@Nullable` | Photo identity |
| `otherIdTypeId` | `Integer` | `@Nullable` | Other ID type |
| `otherIdTypeNo` | `String` | `@Nullable` | Other ID number |
| `contactNo` | `String` | `@Nullable` | Contact number |
| `memberClassificationId` | `String` | `@Nullable` | New category (when changing) |
| `savingsProductId` | `String` | `@Nullable` | Savings product (when changing) |
| `targetAmount` | `BigDecimal` | `@Nullable` | Target amount (when changing) |
| `groupInfoId` | `String` | `@Nullable` | Group |
| `assignedPoId` | `String` | `@Nullable` | Project officer |
| `nominees` | `List<NomineeInfo>` | `@Nullable` | Updated nominees |
| `guarantorInfo` | `GuarantorInfo` | `@Nullable` | Guarantor details |
| `bankId` | `String` | `@Nullable` | Bank |
| `bankBranchId` | `String` | `@Nullable` | Bank branch |
| `bankAccountNumber` | `String` | `@Nullable` | Bank account |
| `routingNumber` | `String` | `@Nullable` | Routing number |
| `tinNumber` | `String` | `@Nullable` | Tax ID |
| `memberCustomField` | `String` | `@Nullable` | Custom field |
| `academicQualificationId` | `String` | `@Nullable` | Qualification |

---

### DDD-REQ-065 — Command: ApproveMemberUpdateCommand

| Field | Type | Constraint | Description |
|-------|------|-----------|-------------|
| `traceId` | `String` | `@NotBlank` | Correlation trace ID |
| `operatorId` | `String` | `@NotBlank` | Acting operator |
| `memberId` | `String` | `@NotBlank` | Member identifier |
| `bufferId` | `String` | `@NotBlank` | DCS buffer identifier |
| `branchInfoId` | `String` | `@NotBlank` | Branch reference |

---

### DDD-REQ-066 — Command: DeleteMemberCommand

| Field | Type | Constraint | Description |
|-------|------|-----------|-------------|
| `traceId` | `String` | `@NotBlank` | Correlation trace ID |
| `operatorId` | `String` | `@NotBlank` | Acting operator |
| `memberId` | `String` | `@NotBlank` | Member to delete |
| `operatorBranchId` | `String` | `@NotBlank` | Operator's branch (for auth guard) |

---

### DDD-REQ-067 — Command: ActivateMemberCommand

| Field | Type | Constraint | Description |
|-------|------|-----------|-------------|
| `traceId` | `String` | `@NotBlank` | Correlation trace ID |
| `operatorId` | `String` | `@NotBlank` | Acting operator |
| `memberIds` | `List<String>` | `@NotEmpty` | Member IDs to activate |
| `officeId` | `String` | `@Nullable` | Office for business date (uses operator office when null) |

---

### DDD-REQ-068 — Command: SaveMemberFamilyCommand

| Field | Type | Constraint | Description |
|-------|------|-----------|-------------|
| `traceId` | `String` | `@NotBlank` | Correlation trace ID |
| `operatorId` | `String` | `@NotBlank` | Acting operator |
| `memberId` | `String` | `@NotBlank` | Member identifier |
| `nominees` | `List<NomineeInfo>` | `@Nullable` | Nominees |
| `guardianInfo` | `GuardianInfo` | `@Nullable` | Guardian |
| `guarantorInfo` | `GuarantorInfo` | `@Nullable` | Guarantor |
| `familyInfo` | `FamilyInfo` | `@Nullable` | Family composition |

---

### DDD-REQ-069 — Command: UpdateMemberFamilyCommand

| Field | Type | Constraint | Description |
|-------|------|-----------|-------------|
| `traceId` | `String` | `@NotBlank` | Correlation trace ID |
| `operatorId` | `String` | `@NotBlank` | Acting operator |
| `memberId` | `String` | `@NotBlank` | Member identifier |
| `familyInfo` | `FamilyInfo` | `@Nullable` | Family composition |
| `otherOrgLoans` | `List<OtherOrganizationLoan>` | `@Nullable` | Other-org loans |

---

### DDD-REQ-070 — Command: UpdateMemberPhotoCommand

| Field | Type | Constraint | Description |
|-------|------|-----------|-------------|
| `traceId` | `String` | `@NotBlank` | Correlation trace ID |
| `operatorId` | `String` | `@NotBlank` | Acting operator |
| `memberId` | `String` | `@NotBlank` | Member identifier |
| `photoReference` | `String` | `@NotBlank` | New photo file reference |
| `supersededPhotoReference` | `String` | `@Nullable` | Prior photo to delete |

---

### DDD-REQ-071 — Command: UpdateMemberSignatureCommand

| Field | Type | Constraint | Description |
|-------|------|-----------|-------------|
| `traceId` | `String` | `@NotBlank` | Correlation trace ID |
| `operatorId` | `String` | `@NotBlank` | Acting operator |
| `memberId` | `String` | `@NotBlank` | Member identifier |
| `signatureReference` | `String` | `@NotBlank` | New signature file reference |
| `supersededSignatureReference` | `String` | `@Nullable` | Prior signature to delete |

---

### DDD-REQ-072 — Command: UpdateMembershipDocumentCommand

| Field | Type | Constraint | Description |
|-------|------|-----------|-------------|
| `traceId` | `String` | `@NotBlank` | Correlation trace ID |
| `operatorId` | `String` | `@NotBlank` | Acting operator |
| `memberId` | `String` | `@NotBlank` | Member identifier |
| `membershipFormReference` | `String` | `@Nullable` | Membership form file ref |
| `nationalIdReference` | `String` | `@Nullable` | National ID doc file ref |
| `passportReference` | `String` | `@Nullable` | Passport file ref |
| `drivingLicenseReference` | `String` | `@Nullable` | Driving licence file ref |
| `photoIdReference` | `String` | `@Nullable` | Photo identity file ref |
| `surveyFormReference` | `String` | `@Nullable` | Survey form file ref |
| `otherFormReference` | `String` | `@Nullable` | Other form file ref |

---

### DDD-REQ-073 — Command: UpdateMemberAssetCommand

| Field | Type | Constraint | Description |
|-------|------|-----------|-------------|
| `traceId` | `String` | `@NotBlank` | Correlation trace ID |
| `operatorId` | `String` | `@NotBlank` | Acting operator |
| `memberId` | `String` | `@NotBlank` | Member identifier |
| `assets` | `List<HouseholdAsset>` | `@Nullable` | Assets to add/update |
| `deletedAssetIds` | `List<String>` | `@Nullable` | Asset IDs to remove |

---

### DDD-REQ-074 — Command: ReassignProjectOfficerCommand

| Field | Type | Constraint | Description |
|-------|------|-----------|-------------|
| `traceId` | `String` | `@NotBlank` | Correlation trace ID |
| `operatorId` | `String` | `@NotBlank` | Acting operator |
| `memberIds` | `List<String>` | `@NotEmpty` | Member IDs to reassign |
| `newPoId` | `String` | `@NotBlank` | New project officer reference |

---

### DDD-REQ-075 — Request DTOs (Presentation)

The Member domain system shall define the following immutable Java `record` Request DTOs in `presentation/controller/dto/`. Each mirrors the corresponding Command fields with HTTP-specific annotations.

**CreateMemberRequest** — mirrors `CreateMemberCommand` fields with `@RequestBody @Valid` on the controller endpoint. Fields match DDD-REQ-062.

**ApproveMemberAdmissionRequest** — mirrors `ApproveMemberAdmissionCommand`. Fields:
- `bufferId: String @NotBlank`
- `passbookNo: String @Nullable`
- `branchInfoId: String @NotBlank`

**UpdateMemberRequest** — mirrors `UpdateMemberCommand` nullable fields.

**ApproveMemberUpdateRequest** — `bufferId: String @NotBlank`, `memberId: String @NotBlank`.

**DeleteMemberRequest** — path parameter `memberId` only; no body.

**ActivateMemberRequest** — `memberIds: List<String> @NotEmpty`, `officeId: String @Nullable`.

**SaveMemberFamilyRequest** — mirrors `SaveMemberFamilyCommand`.

**UpdateMemberFamilyRequest** — mirrors `UpdateMemberFamilyCommand`.

**UpdateMemberPhotoRequest** — `photoReference: String @NotBlank`, `supersededPhotoReference: String @Nullable`.

**UpdateMemberSignatureRequest** — `signatureReference: String @NotBlank`, `supersededSignatureReference: String @Nullable`.

**UpdateMembershipDocumentRequest** — mirrors `UpdateMembershipDocumentCommand` file reference fields.

**UpdateMemberAssetRequest** — mirrors `UpdateMemberAssetCommand`.

**ReassignProjectOfficerRequest** — `memberIds: List<String> @NotEmpty`, `newPoId: String @NotBlank`.

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Module: Member Management", "Module: Member Activation", "Module: Member Project-Officer Reassignment"
> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Module: Member Admission Approval"

---

### DDD-REQ-076 — REST Controller: MemberCommandController

The Member domain system shall implement `MemberCommandController` in `presentation/controller/`, extending `BaseApiController`. It shall inject only `CommandBus`.

For each command endpoint the controller shall:
1. Read `trace_id` from `@RequestAttribute(MdcConstants.TRACE_ID)`.
2. Call `MDC.put("trace_id", trace_id)`.
3. Map the request to a command via `MemberCommandMapper`.
4. Dispatch via `commandBus.handle(command)`.
5. Return `ResponseEntity<ApiResponse>` with `HttpStatus.ACCEPTED` (202).

**Endpoints:**

| Method | Path | Command |
|--------|------|---------|
| `POST` | `/api/members` | `CreateMemberCommand` |
| `POST` | `/api/members/admission/approve` | `ApproveMemberAdmissionCommand` |
| `PUT` | `/api/members/{id}` | `UpdateMemberCommand` |
| `PUT` | `/api/members/admission/update/{id}` | `ApproveMemberUpdateCommand` |
| `DELETE` | `/api/members/{id}` | `DeleteMemberCommand` (soft delete) |
| `PUT` | `/api/members/activate` | `ActivateMemberCommand` |
| `POST` | `/api/members/{id}/family` | `SaveMemberFamilyCommand` |
| `PUT` | `/api/members/{id}/family` | `UpdateMemberFamilyCommand` |
| `PUT` | `/api/members/{id}/photo` | `UpdateMemberPhotoCommand` |
| `PUT` | `/api/members/{id}/signature` | `UpdateMemberSignatureCommand` |
| `PUT` | `/api/members/{id}/documents` | `UpdateMembershipDocumentCommand` |
| `PUT` | `/api/members/{id}/assets` | `UpdateMemberAssetCommand` |
| `PUT` | `/api/members/reassign-po` | `ReassignProjectOfficerCommand` |

> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Cross-Cutting Requirements — Request Handling"
> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Module: Member Admission Approval"

---

## Infrastructure Layer

### DDD-REQ-077 — MongoDB Documents: SOURCE_DATA Snapshots

The Member domain system shall define one MongoDB snapshot document per SOURCE_DATA entity in `infrastructure/persistence/document/`. Each document exposes a `toModel()` method returning the shared domain model via `JsonUtil.convert(this, {EntityName}.class)`.

**PhysicalOfficeInfoDocument** — collection: `physical_office_info_snapshots` (schema in DDD-REQ-045a)

**ProjectInfoDocument** — collection: `project_info_snapshots`

| Field | Java Type | @Field | Description |
|-------|-----------|--------|-------------|
| `id` | `String` | `_id` | Project ID |
| `projectCode` | `String` | `project_code` | Project code |
| `projectName` | `String` | `project_name` | Project name |
| `projectStatus` | `String` | `project_status` | Status enum |
| `associationType` | `String` | `association_type` | GROUP / MEMBER / NA |
| `isActive` | `Boolean` | `is_active` | Active flag |
| `lastEventTimestamp` | `LocalDateTime` | `last_event_timestamp` | Last event time |

Index: `idx_project_code` on `project_code ASC` (unique).

**ProjectPolicyInfoDocument** — collection: `project_policy_info_snapshots` (schema in DDD-REQ-045c)

Index: `idx_project_info_id` on `project_info_id ASC` (for policy-by-project lookup).

**GroupInfoDocument** — collection: `group_info_snapshots` (schema in DDD-REQ-045d)

Index: `idx_group_branch_project` on `branch_info_id ASC, project_info_id ASC`.

**EmployeeCoreInfoDocument** — collection: `employee_core_info_snapshots` (schema in DDD-REQ-045e)

Index: `idx_employee_branch_project` on `branch_info_id ASC, project_info_id ASC`.

**MemberClassificationDocument** — collection: `member_classification_snapshots` (schema in DDD-REQ-045f)

Index: `idx_classification_active` on `is_active ASC`.

**SavingsProductDocument** — collection: `savings_product_snapshots` (schema in DDD-REQ-045g)

Index: `idx_product_code` on `product_code ASC` (unique).

**SavingsProductPolicyDocument** — collection: `savings_product_policy_snapshots` (schema in DDD-REQ-045h)

Index: `idx_savings_product_policy` on `savings_product_id ASC`.

**RelationshipDocument** — collection: `relationship_snapshots` (schema in DDD-REQ-045i)

Index: `idx_relationship_name` on `name ASC` (unique).

**CountryDocument** — collection: `country_snapshots` (schema in DDD-REQ-045j)

Index: `idx_country_code` on `code ASC` (unique).

**MemberStatusDocument** — collection: `member_status_snapshots` (schema in DDD-REQ-045k)

Index: `idx_status_code` on `status_code ASC` (unique).

**OccupationDocument** — collection: `occupation_snapshots` (schema in DDD-REQ-045l)

Index: `idx_occupation_code` on `occupation_code ASC`.

**SavingsAccountDocument** — collection: `savings_account_snapshots` (schema in DDD-REQ-045m)

Index: `idx_savings_member` on `member_id ASC` (unique — one savings account per member).

**ThanaDocument** — collection: `thana_snapshots` (schema in DDD-REQ-045n)

Index: `idx_thana_district` on `district_id ASC, thana_code ASC`.

> 📎 [INFERRED] — required by bits.ddd SOURCE_DATA snapshot pattern

---

### DDD-REQ-078 — Repositories: SOURCE_DATA Snapshot Repositories

The Member domain system shall define one repository per SOURCE_DATA entity in `infrastructure/persistence/repository/`, each extending `MongoRepository<{Entity}Document, String>`.

| Repository | Collection | Key finder methods |
|-----------|-----------|-------------------|
| `PhysicalOfficeInfoRepository` | `physical_office_info_snapshots` | `findByOfficeCode(String code)`, `findByIdAndIsActiveTrue(String id)` |
| `ProjectInfoRepository` | `project_info_snapshots` | `findByProjectCode(String code)`, `findByIdAndIsActiveTrue(String id)` |
| `ProjectPolicyInfoRepository` | `project_policy_info_snapshots` | `findByProjectInfoId(String projectId)` |
| `GroupInfoRepository` | `group_info_snapshots` | `findByIdAndIsActiveTrue(String id)`, `findAllByBranchInfoIdAndProjectInfoId(String, String)` |
| `EmployeeCoreInfoRepository` | `employee_core_info_snapshots` | `findByIdAndIsActiveTrue(String id)`, `findAllByBranchInfoIdAndProjectInfoId(String, String)` |
| `MemberClassificationRepository` | `member_classification_snapshots` | `findByIdAndIsActiveTrue(String id)`, `findAllByIsActiveTrue()` |
| `SavingsProductRepository` | `savings_product_snapshots` | `findByIdAndIsActiveTrue(String id)`, `findByProductCode(String code)` |
| `SavingsProductPolicyRepository` | `savings_product_policy_snapshots` | `findBySavingsProductId(String productId)` |
| `RelationshipRepository` | `relationship_snapshots` | `findAllByIsActiveTrue()`, `findByName(String name)` |
| `CountryRepository` | `country_snapshots` | `findByCode(String code)` |
| `MemberStatusRepository` | `member_status_snapshots` | `findByStatusCode(String code)`, `findAllByIsActiveTrue()` |
| `OccupationRepository` | `occupation_snapshots` | `findByIdAndIsActiveTrue(String id)`, `findAllByIsActiveTrue()` |
| `SavingsAccountRepository` | `savings_account_snapshots` | `findByMemberId(String memberId)` |
| `ThanaRepository` | `thana_snapshots` | `findByIdAndIsActiveTrue(String id)`, `findAllByDistrictId(String districtId)` |

> 📎 [INFERRED] — required by bits.ddd pattern

---

### DDD-REQ-079 — Persistence Configuration: MemberPersistenceConfig

The Member domain system shall define `MemberPersistenceConfig` in `application/config/`. It shall declare a `@Primary @Bean` of type `DomainPersistenceService<Member, String>` using `DefaultDomainPersistenceService`, providing the MongoDB collection name `"members"` for the aggregate.

> 📎 [INFERRED] — required by bits.ddd persistence wiring

---

### DDD-REQ-080 — Queue Configuration: MemberQueueConfig

The Member domain system shall define `MemberQueueConfig` in `infrastructure/messaging/config/`, declaring:

**Exchanges:**
- `member.command.exchange` — topic exchange for inbound commands
- `member.event.exchange` — fanout exchange for outbound domain events

**Queues (per outbound event type):**
- `member.created.queue` → bound to event exchange
- `member.updated.queue` → bound to event exchange
- `member.deleted.queue` → bound to event exchange
- `member.activated.queue` → bound to event exchange
- `member.family.saved.queue` → bound to event exchange
- `member.family.updated.queue` → bound to event exchange
- `member.photo.updated.queue` → bound to event exchange
- `member.signature.updated.queue` → bound to event exchange
- `member.document.updated.queue` → bound to event exchange
- `member.asset.updated.queue` → bound to event exchange
- `member.po.reassigned.queue` → bound to event exchange
- `member.admission.approved.queue` → bound to event exchange
- `member.update.approved.queue` → bound to event exchange
- `member.failed.queue` → bound to event exchange
- Dead-letter queues per command queue

**Source-data inbound queues (one per owning domain event type):**
- `member.physical-office.created.queue`, `member.physical-office.updated.queue`, `member.physical-office.deleted.queue`
- `member.project.created.queue`, `member.project.updated.queue`, `member.project.deleted.queue`
- `member.project-policy.created.queue`, `member.project-policy.updated.queue`, `member.project-policy.deleted.queue`
- `member.group.created.queue`, `member.group.updated.queue`, `member.group.deleted.queue`
- `member.employee.created.queue`, `member.employee.updated.queue`, `member.employee.deleted.queue`
- `member.member-classification.created.queue`, `member.member-classification.updated.queue`, `member.member-classification.deleted.queue`
- `member.savings-product.created.queue`, `member.savings-product.updated.queue`, `member.savings-product.deleted.queue`
- `member.savings-product-policy.created.queue`, `member.savings-product-policy.updated.queue`, `member.savings-product-policy.deleted.queue`
- `member.relationship.created.queue`, `member.relationship.updated.queue`, `member.relationship.deleted.queue`
- `member.savings-account.created.queue`, `member.savings-account.updated.queue`, `member.savings-account.deleted.queue`
- `member.thana.created.queue`, `member.thana.updated.queue`, `member.thana.deleted.queue`

**RabbitMQRouteRegistry** shall register routing keys for each domain event class.

> 📎 [INFERRED] — required by bits.ddd messaging wiring

---

### DDD-REQ-081 — Bootstrap: MemberCommandApplication

The Member domain system shall define a `MemberCommandApplication` class annotated `@SpringBootApplication`. The annotation-processor-driven bean discovery shall ensure `@RegisterCommandHandler`, `@PersistDomain`, and `@MongoDomainRepo` annotations are processed without requiring explicit `scanBasePackages`.

> 📎 [INFERRED]

---

## Cross-Cutting Requirements

### DDD-REQ-082 — Audit and Record Lifecycle [UNCHANGED]

The Member domain system shall record, on every record it first persists, the identifier of the acting operator as the creating user and the identifier of the acting operator as the last-updating user, together with the system creation and last-update timestamps.

The Member domain system shall, whenever it modifies an existing record, set the last-updating user to the acting operator and refresh the last-update timestamp to the current system date and time.

The Member domain system shall maintain an optimistic-locking version on persisted records so that a record changed concurrently by another transaction cannot be silently overwritten.

The Member domain system shall treat each member record and its associated savings account, fee transactions, membership history and status-change history as logically active or inactive by means of a domain-status indicator, where the active state is represented by the value one and the inactive state by the value two.

When a member is deleted, the Member domain system shall mark the member record, and the related records it cascades to, as inactive by setting their domain-status indicator to the inactive value rather than physically removing them.

The Member domain system shall persist a member's nominees and passbooks as dependent children of the member such that they are created, updated and removed together with the member, with orphaned children deleted.

The Member domain system shall persist a member's contact addresses as dependent children of the contact information, and a family's other-organisation loans as dependent children of the family information, such that they are created, updated and removed together with their parent.

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Cross-Cutting Requirements — Audit and Record Lifecycle" [UNCHANGED]
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Cross-Cutting Requirements — Audit and Record Lifecycle" [UNCHANGED]

---

### DDD-REQ-083 — Authentication and Authorisation [UNCHANGED]

Where the requesting session is not authenticated, the Member domain system shall deny access to every operation and redirect the actor to the login flow before any member operation is performed.

Where an operation is invoked as an asynchronous (AJAX) request without an authenticated session, the Member domain system shall reject the request as unauthorised.

The Member domain system shall bind every member operation to the microfinance business module for the purpose of session and permission resolution.

The Member domain system shall enforce database-driven feature-and-action access rules; if an authenticated operator attempts an operation without the required feature-action permission, the system shall reject the request before any business logic executes.

When a member is registered, the Member domain system shall require the acting office to be a branch office, and if the acting office is not a branch office the system shall reject the registration with the message "Member creation only possible at Branch Office".

When a member deletion is requested, the Member domain system shall require the acting office to be the member's own branch office, and if it is not the system shall reject the deletion with the message "You are not authorized to delete this member".

When a member profile or member detail is opened for viewing or editing by an internal user, the Member domain system shall permit access only when the member's project is among the user's permitted projects, or the member's project and branch office are both among the user's permitted projects and offices, or the user holds the microfinance programme-administrator role; otherwise the system shall reject the request.

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Cross-Cutting Requirements — Authentication and Authorisation" [UNCHANGED]
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Cross-Cutting Requirements — Authentication and Authorisation" [UNCHANGED]

---

### DDD-REQ-084 — Request Handling [UNCHANGED]

The Member domain system shall accept only standard read or submit request methods for its operations and shall reject any request made with another method.

The Member domain system shall return the outcome of operations as structured message payloads containing a result type (success or error), a message title, and a message body.

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Cross-Cutting Requirements — Request Handling" [UNCHANGED]
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Cross-Cutting Requirements — Request Handling" [UNCHANGED]

---

### DDD-REQ-085 — Operational Cross-Cuts [UNCHANGED]

While a new admission is being validated and provisioned, the Member domain system shall hold a concurrency lock keyed on the applicant's identity-document number and a second lock keyed on the combination of branch, project and group, so that two simultaneous requests for the same applicant or the same branch-project-group cannot both proceed.

When a new-admission validation ends — whether it succeeds, fails, or raises an error — the Member domain system shall release both the identity-document lock and the branch-project-group lock.

The Member domain system shall apply a registration lock expiry of thirty minutes so that an abandoned registration does not block subsequent attempts indefinitely.

After a member is created or updated and committed, the Member domain system shall publish the member's basic, personal, project and contact information, and the member's savings-account information, to a downstream message queue, tagging the publication as a creation or an update as appropriate.

After a member, family, photo, signature, document or asset change is committed and any automatic accounting events are produced, the Member domain system shall process those accounting voucher events asynchronously.

After a photo, signature, family-photo or membership-document change is committed, the Member domain system shall move the newly uploaded files from the temporary area into the member's permanent storage location, delete superseded files, clean up temporary files, and if any file relocation fails the system shall report a file-relocation error without rolling back the committed data.

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Cross-Cutting Requirements — Operational Cross-Cuts" [UNCHANGED]
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Cross-Cutting Requirements — Operational Cross-Cuts" [UNCHANGED]

---

### DDD-REQ-086 — Error Response Format [UNCHANGED]

The Member domain system shall return operation outcomes as a structured message payload containing a result type (success or error), a message title, and a message body; for member registration the system additionally returns the list of suspected duplicate members when a duplicate is detected, and for domain-constraint failures it returns the offending field-level error text.

The Member domain system shall resolve user-facing message text from the microfinance message catalogue by message key; when a referenced key is not defined in the catalogue the system shall present the key itself as the message text.

The Member domain system shall map error conditions to message text as follows (combined from both EARS files, English only):

| Error condition | Message text (verbatim) |
|----------------|------------------------|
| Branch code missing | "Branch is required" |
| Project code missing | "Project is required" |
| Member category missing | "Member Category is required" |
| Project code not found | "Request Parameter Field 'Project' is not valid" |
| Group required but identifier missing | "Request Parameter Field 'VO' is not provided" |
| Field officer required but identifier missing | "Request Parameter Field 'PO' is not provided" |
| Member name missing | "Member Name is required" |
| Member name fails name format | "'Member Name' format not supported" |
| Gender missing | "Gender is required" |
| Date of birth missing | "Date of Birth is required" |
| Date of birth fails date format | "'Date of Birth' format not supported" |
| Occupation missing | "Occupation is required" |
| Father name missing | "Father Name is required" / "Enter Father Name" |
| Father name fails name format | "'Father Name' format not supported" |
| Mother name missing | "Mother Name is required" / "Enter Mother Name" |
| Mother name fails name format | "'Mother Name' format not supported" |
| Mobile number missing | "Mobile Number is required" |
| Mobile number fails format | "'Mobile Number' format not supported" |
| Marital status missing | "Marital Status is required" |
| Married and spouse name missing | "Spouse Name is required" / "Enter Spouse Name for Married Member" |
| Married and spouse name fails format | "'Spouse Name' format not supported" |
| Married and spouse DOB missing | "Spouse Date of Birth is required" |
| Married and spouse DOB bad format | "'Spouse Date of Birth' format not supported" |
| Permanent address missing | "Permanent Address is required" |
| Present address missing | "Present Address is required" |
| Permanent upazila missing | "Permanent Upazila is required" |
| Present upazila missing | "Present Upazila is required" |
| Savings product missing | "Savings Product is required" |
| Target amount missing | "Target Amount is required" |
| Identity card missing for a party | "\<Actor\> ID Card is required" |
| Identity card type missing | "\<Actor\> Card Type Required" |
| Identity card number missing | "\<Actor\> Card No Required" |
| National ID not 13 or 17 digits | "\<Actor\> National ID must be 13/17 digit" |
| Smart Card not 10 digits | "\<Actor\> Smart Card ID must be 10 digit" |
| Other ID > 20 characters | "\<Actor\> Other Id Number Length is incorrect, it should be less then 20 characters." |
| Passport expiry missing/bad format | "\<Actor\> Passport Expiry Date Required or Format not correct" |
| Guarantor DOB bad format | "Guarantor 'Date of Birth' format not supported" |
| Guarantor ID card missing | "Guarantor 'ID Card' required" |
| Guarantor name empty | "Guarantor 'Name' can not be empty" |
| Nominee ID card missing | "Nominee 'ID Card' is required!" |
| Nominee name key missing | "Nominee 'name' is required!" |
| Nominee relationship key missing | "Nominee 'Relationship' is required!" |
| Nominee DOB bad format | "Nominee 'Date of Birth' format not supported" |
| Member and spouse same identity | "Member and Spouse \<Card Type\> can not be the same!" |
| Member and guarantor same identity | "Member and Guarantors \<Card Type\> can not be the same!" |
| Application date later than business date | "Application date should be lower than or equal to business date." |
| Branch not a valid office | "Request Parameter Field 'Branch' is not valid" |
| Office not a Branch Office | "Member creation only possible at Branch Office" |
| Business day not open | "Business Day of this office is not open" |
| Category not valid for project | "Request Parameter Field 'Category' is not valid" |
| Creation already in progress (lock held) | "Member Creation process is on going. Please wait and check after few minutes." |
| National ID already registered | "Same National ID already exists for another member." |
| Smart Card already registered | "Same Smart Card ID already exists for another member." |
| Driving-licence/photo ID already registered | "Same Birth Certificate Number already exists for another member." |
| Spouse NID already registered as another's spouse | "A spouse is already exist with given spouse national id" |
| Present upazila not valid | "Request Parameter Field 'Present Upazila' is not valid" |
| Permanent upazila not valid | "Request Parameter Field 'Permanent Upazila' is not valid" |
| Savings product not valid | "Request Parameter Field 'Savings Product' is not valid" |
| Occupation not valid | "Request Parameter Field 'Occupation' is not valid" |
| Academic qualification not valid | "Request Parameter Field 'Academic Qualification' is not valid" |
| Gender not valid | "Request Parameter Field 'Gender' is not valid" |
| DOB on or after business date | "Date of Birth must be previous to business date" |
| Member age outside category range | "Member category does not allow specified member age" |
| Group not valid or not in branch | "Request Parameter Field 'VO' is not valid" |
| Group gender policy disallows gender | "VO policy does not allow specified gender" |
| Group not Active | "Request Parameter Field 'VO' is not Active" |
| Field officer not assignable | "Invalid PO!" |
| Target amount below minimum | "Request Parameter Field 'Target Amount' is not valid. Target amount can not less than minimum target amount" |
| Guarantor NID not National ID (admission) | "Request Parameter Field 'Guarantor National ID' is not valid" |
| Guarantor relationship not valid | "Request Parameter Field 'Guarantor Relationship' is not valid" |
| Guarantor relationship is spouse while unmarried | "Relationship can not be spouse while member is unmarried for Guarantor" |
| Nominee name empty | "Request Parameter Field 'Nominee Name' can not be empty" |
| Nominee relationship not valid | "Request Parameter Field 'Nominee Relationship' is not valid" |
| Nominee relationship is spouse while unmarried | "Relationship can not be spouse while member is unmarried for Nominee" |
| Member ID missing (update) | "Member ID can not be empty" |
| Member not found (update) | "Member is not valid" / "Member is not found" |
| Savings product changed with transactions | "Member has already savings account transaction" |
| 4th nominee attempted | "All 3 nominees are created. You can not add more" |
| Referenced nominee not found | "Existing nominee not found with provided information" |
| Downstream DCS call failed | "Please contact with your system administrator" |
| Member not found (delete/view) | "Member not found" |
| Inactive member update attempted | "Member {0} can not be update because of its inactive status" |
| Delete closed member | "Can not Delete closed member !!" |
| Delete from wrong office | "You are not authorized to delete this member" |
| Delete with loan proposal | "Can not Delete because of existing loan process !!" |
| Delete with savings transactions | "Can not Delete because of existing savings process !!" |
| Nominee spouse while unmarried | "Relationship can not be spouse while member is unmarried" |
| Guardian NID not 13/17 digits | "National ID must be 13/17 digit" |
| Minor nominee without guardian | "Guardian is required for nominee with age below 18" |
| Guarantor NID format invalid | "National ID must be 13/17 digit" |
| No data to save (family form) | "No data found to save" |
| No members to activate | "No Member found to activate" |
| PO reassignment failed | "Member's PO Information unable to update" |
| Target amount below minimum (management) | "Target amount can not less than minimum target amount" |
| Group (VO) not supplied | "VO information is mandatory" |
| No contact number | "Contact number is required" |
| Member category changed with outstanding loan | "Member category can not be changed. Member has outstanding loan balance." |
| Member category changed with pending proposal | "Member category can not be changed. Member has a pending loan proposal." |
| DeDupe API error | "DeDupe API Error. \n" + error detail |
| Duplicate member found | "Duplicate Information Found" |
| Duplicate with identity change (non-admin) | "Duplicate Information Found. You Can Not Change any of National ID/Birth Certificate Number/Passport No/Smart Card ID/Driving License No" |
| Successful member creation | "Member Setup {0} is created successfully" |
| Successful member update | "{0} Member is updated successfully" |
| Successful member deletion | "{0} Member is deleted successfully" |
| Successful PO reassignment | "Member's PO Information has been updated successfully" |
| Successful admission | "Member successfully saved" |
| Successful update approval | "Member successfully updated" |
| Update validation failure (DCS) | "Member Rejected to admit into system" |

> 📎 Source: MemberAdmissionApproval-EARS-Specification.md § "Cross-Cutting Requirements — Error Response Format" [UNCHANGED]
> 📎 Source: MemberManagement-EARS-Specification-Resolved.md § "Cross-Cutting Requirements — Error Response Format" [UNCHANGED]

---

## Out of Scope

The following capabilities were identified in the source EARS but are explicitly excluded from this DDD/CQRS/ES specification. They should be handled separately or deferred.

| # | Capability | Source | Reason |
|---|-----------|--------|--------|
| 1 | Event Sourcing snapshotting (snapshot every N events) | [INFERRED] | State database used (`members` collection); event replay is not required |
| 2 | Accounting voucher event processing (automatic vouchers on create/update/delete) | MemberManagement § "Cross-Cutting Requirements — Operational Cross-Cuts" | Async side effect delegated to accounting domain upon receiving domain events; out of Member aggregate boundary [OUT-OF-SCOPE] |
| 3 | Savings account creation (compulsory savings account on registration) | MemberManagement § "Module: Member Management — Member Registration" | Created by savings domain upon receiving `MemberCreatedEvent`; out of Member aggregate boundary [OUT-OF-SCOPE] |
| 4 | Membership fee transaction creation | MemberManagement § "Module: Member Management — Member Registration" | Created by accounting domain upon receiving `MemberCreatedEvent`; out of Member aggregate boundary [OUT-OF-SCOPE] |
| 5 | File relocation to permanent storage | MemberManagement § "Cross-Cutting Requirements — Operational Cross-Cuts" | Infrastructure file-system concern; handled asynchronously by the handler after persist [ASYNC] |
| 6 | Employee-member history (recording release of prior PO on update) | MemberManagement § "Module: Member Management — Member Profile Update" | HR domain concern; triggered via domain event [OUT-OF-SCOPE] |
| 7 | External insurance service age validation | MemberManagement § "Module: Member Validation Rules" | External service integration — call made by handler pre-aggregate; result injected into spec context |
| 8 | DCS member channel HTTP calls | MemberAdmissionApproval § "Module: Cross-Plugin and External Integrations — DCS Member Channel" | External HTTP integration; handled by ApproveMemberAdmission/ApproveMemberUpdate handlers; not a domain concern |

---

## Open Questions

| ID | Source | Question | Status |
|----|--------|----------|--------|
| Q-1 | MemberAdmissionApproval § "Domain Entities — Member Address" vs. MemberManagement § "Domain Entities — MemberAddress" | **MemberAddress definition conflict:** MemberAdmissionApproval defines 5 fields (addressTitle, address, thanaId, cityId, contactInfo); MemberManagement defines 7 fields (adds countryId, zipCode). Which is the authoritative schema? The union (7 fields) is used in this spec. | OPEN |
| Q-2 | MemberAdmissionApproval Open Questions §1 | **Externally-referenced entities not catalogued:** Full field sets of `PhysicalOfficeInfo`, `EmployeeCoreInfo`, `GroupInfo`, `MemberStatus`, `DomainStatus`, `CloseReason`, `Salutation`, `Occupation`, `MemberGender`, `AcademicQualification`, `PassbookStatus`, `SavingsAccount`, `ProgramInfo`, `SuspectedMember` were not fully extracted. Snapshot schemas in this spec use only fields referenced in validation logic. | OPEN |
| Q-3 | MemberAdmissionApproval Open Questions §2 | **Savings product resolution on update:** When the supplied savings product cannot be resolved (member update), the codebase builds an error result but does not return it (processing continues). Is this an intentional non-blocking warning or a defect? | OPEN |
| Q-4 | MemberAdmissionApproval Open Questions §3 | **Identity-uniqueness rejection message placeholder:** Messages "Same National ID already exists for another member." etc. carry no `{0}` placeholder, yet a detailed suspected-member description (office, group, member) is passed as an argument. Should the catalogue messages include `{0}` for the description? | OPEN |
| Q-5 | MemberManagement § "Cross-Cutting Requirements — Error Response Format" | **MemberAddress `passbook_number_in_use` message uses {0}, {1}, {2} placeholders** (MemberNo, VoName, PassbookNo) — confirm the placeholder ordering and whether VoName is nullable. | OPEN |
