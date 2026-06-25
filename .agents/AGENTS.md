# Workspace Coding Rules and Constraints

Always apply the following rules when working on the `bits-member-command` project:

## Architecture and Library Compliance
* **Prioritize `bits-ddd-lib` 1.2.0 Patterns**: When there is a conflict between DDD instructions in `docs/Member-Command-DDD-EARS.md` (originally written for 1.0.7) and the `bits-ddd-lib` version 1.2.0 API, always prioritize the library's design:
  * **Source Data Provider**: Implement `SourceDataProvider<Command>` and use `SourceDataCoordinator` to concurrently fetch lookup snapshots. Do not use legacy sequential service helper classes.
  * **Mutable Source Data snapshots**: Ensure snapshot classes extend `com.bits.ddd.domain.sourcedata.SourceData` and are annotated with `@MongoSourceData`. Avoid using immutable Java records for snapshots as they fail to compile with the library's mutable tenant setters.
  * **Persistence Wiring**: Use field injection with `@PersistDomain` on the handler class to automatically generate the transactional `DomainPersistenceService` bean.

## Business Rule Integrity
* **Preserve EARS Business Logic**: Keep all business logic and invariants defined in `docs/Member-Command-DDD-EARS.md` intact:
  * Implement the 8 specific domain validation specs in `domain/specification/rules/`.
  * Concatenate first, middle, and last names cleanly to derive the full name.
  * Redistribute nominee shares equally to exactly 2 decimal places.
  * Initialize the member lifecycle audit trail (`MembershipStatusChangeHistory`) from zero status to Active.
