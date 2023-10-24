package com.practic.back.testtz.service.accountAudit;

import com.practic.back.testtz.entity.AccountAudit;

import java.util.List;
import java.util.UUID;

public interface AccountAuditService {
    void saveAccountAudit(AccountAudit audit);
    List<AccountAudit> getAccountAuditByAccountId(UUID id);
}
