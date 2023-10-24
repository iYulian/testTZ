package com.practic.back.testtz.repository;

import com.practic.back.testtz.entity.AccountAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountAuditRepository extends JpaRepository<AccountAudit, UUID> {
    List<AccountAudit> getAccountAuditsByChangedAccountId(UUID id);
}
