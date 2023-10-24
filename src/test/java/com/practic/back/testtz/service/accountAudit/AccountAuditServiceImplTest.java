package com.practic.back.testtz.service.accountAudit;

import com.practic.back.testtz.entity.AccountAudit;
import com.practic.back.testtz.exception.NotFoundRequestException;
import com.practic.back.testtz.repository.AccountAuditRepository;
import com.practic.back.testtz.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AccountAuditServiceImplTest {
    @InjectMocks
    private AccountAuditServiceImpl accountAuditService;
    @Mock
    private AccountAuditRepository auditRepository;
    @Mock
    private AccountRepository accountRepository;
    private AutoCloseable closeable;
    @BeforeEach
    public void openMocks() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void releaseMocks() throws Exception {
        closeable.close();
    }
    @Test
    @Tag("addAccountAudit")
    void saveAccountAudit(){
        LocalDateTime dateTime = LocalDateTime.now();
        UUID accountId = UUID.randomUUID();
        AccountAudit audit = AccountAudit.builder()
                .newAccountJson("123")
                .operationType("Create")
                .modifiedAt(dateTime)
                .changedAccountId(accountId)
                .build();
        accountAuditService.saveAccountAudit(audit);
        Mockito.verify(auditRepository, Mockito.times(1)).save(audit);
    }

    @Test
    void getAccountAuditByAccountId() {
        UUID accountId = UUID.randomUUID();
        List<AccountAudit> audits = new ArrayList<>();
        LocalDateTime dateTime = LocalDateTime.now();

        Mockito.when(accountRepository.existsById(accountId)).thenReturn(false);
        Throwable notFE1 = Assertions.assertThrows(NotFoundRequestException.class,
                () -> accountAuditService.getAccountAuditByAccountId(accountId));
        Assertions.assertNotNull(notFE1);
        Assertions.assertEquals(NotFoundRequestException.class, notFE1.getClass());
        Assertions.assertEquals("Не существует счета с id - " + accountId, notFE1.getMessage());

        Mockito.when(accountRepository.existsById(accountId)).thenReturn(true);
        Mockito.when(auditRepository.getAccountAuditsByChangedAccountId(accountId)).thenReturn(audits);
        Throwable notFE2 = Assertions.assertThrows(NotFoundRequestException.class,
                () -> accountAuditService.getAccountAuditByAccountId(accountId));
        Assertions.assertNotNull(notFE2);
        Assertions.assertEquals(NotFoundRequestException.class, notFE2.getClass());
        Assertions.assertEquals("Не нет данных об изменении счета с id - " + accountId, notFE2.getMessage());

        AccountAudit audit1 = AccountAudit.builder()
                .id(UUID.randomUUID())
                .newAccountJson("123")
                .operationType("Create")
                .modifiedAt(dateTime)
                .changedAccountId(accountId)
                .build();
        AccountAudit audit2 = AccountAudit.builder()
                .id(UUID.randomUUID())
                .newAccountJson("123")
                .operationType("Create")
                .modifiedAt(dateTime)
                .changedAccountId(accountId)
                .build();
        audits.add(audit1);
        audits.add(audit2);
        Mockito.when(auditRepository.getAccountAuditsByChangedAccountId(accountId)).thenReturn(audits);
        List<AccountAudit> resultAudits = accountAuditService.getAccountAuditByAccountId(accountId);
        Assertions.assertNotNull(resultAudits);
        Assertions.assertEquals(resultAudits.get(0), audits.get(0));
        Assertions.assertEquals(resultAudits.get(1), audits.get(1));
    }
}