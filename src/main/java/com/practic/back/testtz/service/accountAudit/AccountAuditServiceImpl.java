package com.practic.back.testtz.service.accountAudit;

import com.practic.back.testtz.entity.AccountAudit;
import com.practic.back.testtz.exception.NotFoundRequestException;
import com.practic.back.testtz.repository.AccountAuditRepository;
import com.practic.back.testtz.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AccountAuditServiceImpl implements AccountAuditService{
    private final AccountAuditRepository auditRepository;
    private final AccountRepository accountRepository;
    @Override
    @Transactional
    public void saveAccountAudit(AccountAudit audit) {
        auditRepository.save(audit);
        log.info("Изменения в счете сохранены в аудит");
    }

    @Override
    @Transactional
    public List<AccountAudit> getAccountAuditByAccountId(UUID id) {
        if (!accountRepository.existsById(id)){
            log.error("Данные аудита не могут быть получены");
            throw new NotFoundRequestException("Не существует счета с id - " + id);
        }
        List<AccountAudit> audit = auditRepository.getAccountAuditsByChangedAccountId(id);
        if (audit.isEmpty()){
            log.error("Данные аудита не могут быть получены");
            throw new NotFoundRequestException("Не нет данных об изменении счета с id - " + id);
        }
        return audit;
    }
}
