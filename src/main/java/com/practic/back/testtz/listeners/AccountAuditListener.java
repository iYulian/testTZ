package com.practic.back.testtz.listeners;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practic.back.testtz.entity.Account;
import com.practic.back.testtz.entity.AccountAudit;
import com.practic.back.testtz.service.accountAudit.AccountAuditService;
import com.practic.back.testtz.util.BeanUtil;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


public class AccountAuditListener {
    private final ObjectMapper mapper = new ObjectMapper();

    @PostPersist
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void preCreate(Account account) {
        try {
            BeanUtil.getBeans(AccountAuditService.class).saveAccountAudit(AccountAudit.builder()
                    .newAccountJson(mapper.writeValueAsString(account))
                    .operationType("Create")
                    .modifiedAt(LocalDateTime.now())
                    .changedAccountId(account.getId())
                    .build()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @PostUpdate
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void preUpdate(Account account) {
        try {
            BeanUtil.getBeans(AccountAuditService.class).saveAccountAudit(AccountAudit.builder()
                    .newAccountJson(mapper.writeValueAsString(account))
                    .operationType("Change")
                    .modifiedAt(LocalDateTime.now())
                    .changedAccountId(account.getId())
                    .build()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
