package com.practic.back.testtz.service.account;

import com.practic.back.testtz.dto.AccountAddDto;
import com.practic.back.testtz.dto.AccountInfDto;
import com.practic.back.testtz.dto.DepositDto;
import com.practic.back.testtz.dto.TransferDto;
import com.practic.back.testtz.dto.WithdrawDto;
import com.practic.back.testtz.entity.Account;
import com.practic.back.testtz.exception.NotFoundRequestException;
import com.practic.back.testtz.exception.ValidRequestException;
import com.practic.back.testtz.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class AccountServiceImplTest {

    @InjectMocks
    private AccountServiceImpl accountService;
    @Mock
    private PasswordEncoder passwordEncoder;
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
    void getAccounts() {
        Account account1 = new Account();
        account1.setId(UUID.randomUUID());
        account1.setPin("1111");
        account1.setBeneficiaryName("Ivan");
        account1.setAmount(BigDecimal.valueOf(9999.99));

        Account account2 = new Account();
        account2.setId(UUID.randomUUID());
        account2.setPin("9999");
        account2.setBeneficiaryName("Petr");
        account2.setAmount(BigDecimal.valueOf(1.11));

        List<Account> accountList = new ArrayList<>();
        accountList.add(account1);
        accountList.add(account2);

        Mockito.when(accountRepository.findAll()).thenReturn(new ArrayList<>());
        Throwable notFE1 = Assertions.assertThrows(NotFoundRequestException.class,
                () -> accountService.getAccounts());
        Assertions.assertNotNull(notFE1);
        Assertions.assertEquals(NotFoundRequestException.class, notFE1.getClass());
        Assertions.assertEquals("Нет ни одного счета", notFE1.getMessage());

        Mockito.when(accountRepository.findAll()).thenReturn(accountList);
        List<AccountInfDto> resultAccountList = accountService.getAccounts();
        Assertions.assertNotNull(resultAccountList);
        Assertions.assertEquals(resultAccountList.get(0).getBeneficiaryName(), accountList.get(0).getBeneficiaryName());
        Assertions.assertEquals(resultAccountList.get(0).getAmount(), accountList.get(0).getAmount());
        Assertions.assertEquals(resultAccountList.get(1).getBeneficiaryName(), accountList.get(1).getBeneficiaryName());
        Assertions.assertEquals(resultAccountList.get(1).getAmount(), accountList.get(1).getAmount());
    }

    @Test
    void addAccount() {
        AccountAddDto addDto = new AccountAddDto();
        addDto.setPin("1234");
        addDto.setBeneficiaryName("Sergey");

        Mockito.when(passwordEncoder.encode(Mockito.any(String.class))).thenReturn("BCryptPassword");
        Account accountResult = accountService.addAccount(addDto);

        Mockito.verify(accountRepository, Mockito.times(1)).save(Mockito.any(Account.class));
        Assertions.assertNotNull(accountResult);
        Assertions.assertEquals(accountResult.getAmount(), BigDecimal.valueOf(0));
        Assertions.assertEquals(accountResult.getBeneficiaryName(), "Sergey");
        Assertions.assertEquals("BCryptPassword", accountResult.getPin());

    }

    @Test
    void depositAccount() {
        UUID id = UUID.randomUUID();
        BigDecimal accountAmount = BigDecimal.valueOf(100);

        DepositDto dto = new DepositDto();
        dto.setAccountIdTo(id);
        dto.setAmount(BigDecimal.valueOf(1.01));

        Account account = new Account();
        account.setId(id);
        account.setBeneficiaryName("Andrey");
        account.setAmount(accountAmount);

        Mockito.when(accountRepository.existsById(dto.getAccountIdTo())).thenReturn(false);

        Throwable validRE1 = Assertions.assertThrows(ValidRequestException.class,
                () -> accountService.depositAccount(dto));
        Assertions.assertNotNull(validRE1);
        Assertions.assertEquals(ValidRequestException.class, validRE1.getClass());
        Assertions.assertEquals("Не существует банковского счета с id - " + dto.getAccountIdTo(), validRE1.getMessage());

        Mockito.when(accountRepository.existsById(dto.getAccountIdTo())).thenReturn(true);
        Mockito.when(accountRepository.getReferenceById(dto.getAccountIdTo())).thenReturn(account);
        Account accountResult = accountService.depositAccount(dto);

        Mockito.verify(accountRepository, Mockito.times(1)).save(Mockito.any(Account.class));
        Assertions.assertNotNull(accountResult);
        Assertions.assertEquals(accountResult.getId(), id);
        Assertions.assertEquals(accountResult.getAmount(), accountAmount.add(dto.getAmount()));
    }

    @Test
    void withdrawAccount() {
        UUID id = UUID.randomUUID();
        BigDecimal accountAmount = BigDecimal.valueOf(100);

        WithdrawDto dto = new WithdrawDto();
        dto.setAccountIdFrom(id);
        dto.setAmount(accountAmount.add(BigDecimal.valueOf(0.01)));
        dto.setPin("555");

        Account account = new Account();
        account.setId(id);
        account.setBeneficiaryName("Vasiliy");
        account.setAmount(accountAmount);

        Mockito.when(accountRepository.getReferenceById(dto.getAccountIdFrom())).thenReturn(account);
        Mockito.when(accountRepository.existsById(dto.getAccountIdFrom())).thenReturn(false);
        Mockito.when(passwordEncoder.matches(dto.getPin(), account.getPin())).thenReturn(false);

        Throwable validRE1 = Assertions.assertThrows(ValidRequestException.class,
                () -> accountService.withdrawAccount(dto));
        Assertions.assertNotNull(validRE1);
        Assertions.assertEquals(ValidRequestException.class, validRE1.getClass());
        Assertions.assertEquals("Не существует банковского счета с id - " + dto.getAccountIdFrom(), validRE1.getMessage());

        Mockito.when(accountRepository.existsById(dto.getAccountIdFrom())).thenReturn(true);

        Throwable validRE2 = Assertions.assertThrows(ValidRequestException.class,
                () -> accountService.withdrawAccount(dto));
        Assertions.assertNotNull(validRE2);
        Assertions.assertEquals(ValidRequestException.class, validRE2.getClass());
        Assertions.assertEquals("Введен неверный пароль", validRE2.getMessage());

        Mockito.when(passwordEncoder.matches(dto.getPin(), account.getPin())).thenReturn(true);
        Throwable validRE3 = Assertions.assertThrows(ValidRequestException.class,
                () -> accountService.withdrawAccount(dto));
        Assertions.assertNotNull(validRE3);
        Assertions.assertEquals(ValidRequestException.class, validRE3.getClass());
        Assertions.assertEquals("На счете недостаточно средств", validRE3.getMessage());

        dto.setAmount(accountAmount);
        Account accountResult1 = accountService.withdrawAccount(dto);
        Mockito.verify(accountRepository, Mockito.times(1)).save(Mockito.any(Account.class));
        Assertions.assertEquals(accountResult1.getId(), id);
        Assertions.assertEquals(accountResult1.getAmount(), accountAmount.subtract(dto.getAmount()));

        account.setAmount(accountAmount);
        dto.setAmount(accountAmount.subtract(BigDecimal.valueOf(0.01)));
        Account accountResult2 = accountService.withdrawAccount(dto);
        Mockito.verify(accountRepository, Mockito.times(2)).save(Mockito.any(Account.class));
        Assertions.assertEquals(accountResult2.getId(), id);
        Assertions.assertEquals(accountResult2.getAmount(), accountAmount.subtract(dto.getAmount()));
    }

    @Test
    void transferAccount() {

        UUID accountFromId = UUID.randomUUID();
        UUID accountToId = UUID.randomUUID();
        BigDecimal accountFromAmount = BigDecimal.valueOf(100);
        BigDecimal accountToAmount = BigDecimal.valueOf(100);

        TransferDto dto = new TransferDto();
        dto.setAccountIdFrom(accountFromId);
        dto.setAccountIdTo(accountToId);
        dto.setAmount(accountFromAmount.add(BigDecimal.valueOf(1000000000.01)));

        Account accountFrom = new Account();
        accountFrom.setId(accountFromId);
        accountFrom.setBeneficiaryName("Vitaliy");
        accountFrom.setAmount(accountFromAmount);

        Account accountTo = new Account();
        accountTo.setId(accountToId);
        accountTo.setBeneficiaryName("Vera");
        accountTo.setAmount(accountToAmount);

        Mockito.when(accountRepository.getReferenceById(dto.getAccountIdFrom())).thenReturn(accountFrom);
        Mockito.when(accountRepository.getReferenceById(dto.getAccountIdTo())).thenReturn(accountTo);
        Mockito.when(accountRepository.existsById(dto.getAccountIdTo())).thenReturn(false);
        Mockito.when(accountRepository.existsById(dto.getAccountIdFrom())).thenReturn(false);
        Mockito.when(passwordEncoder.matches(dto.getPin(), accountFrom.getPin())).thenReturn(false);

        Throwable validRE = Assertions.assertThrows(ValidRequestException.class,
                () -> accountService.transferAccount(dto));
        Assertions.assertNotNull(validRE);
        Assertions.assertEquals(ValidRequestException.class, validRE.getClass());
        Assertions.assertEquals("Не существует банковского счета с id - " + dto.getAccountIdTo(), validRE.getMessage());

        Mockito.when(accountRepository.existsById(dto.getAccountIdTo())).thenReturn(false);
        Mockito.when(accountRepository.existsById(dto.getAccountIdFrom())).thenReturn(true);

        Throwable validRE1 = Assertions.assertThrows(ValidRequestException.class,
                () -> accountService.transferAccount(dto));
        Assertions.assertNotNull(validRE1);
        Assertions.assertEquals(ValidRequestException.class, validRE1.getClass());
        Assertions.assertEquals("Не существует банковского счета с id - " + dto.getAccountIdTo(), validRE1.getMessage());

        Mockito.when(accountRepository.existsById(dto.getAccountIdTo())).thenReturn(true);
        Mockito.when(accountRepository.existsById(dto.getAccountIdFrom())).thenReturn(false);

        Throwable validRE2 = Assertions.assertThrows(ValidRequestException.class,
                () -> accountService.transferAccount(dto));
        Assertions.assertNotNull(validRE2);
        Assertions.assertEquals(ValidRequestException.class, validRE2.getClass());
        Assertions.assertEquals("Не существует банковского счета с id - " + dto.getAccountIdFrom(), validRE2.getMessage());

        Mockito.when(accountRepository.existsById(dto.getAccountIdFrom())).thenReturn(true);

        Throwable validRE3 = Assertions.assertThrows(ValidRequestException.class,
                () -> accountService.transferAccount(dto));
        Assertions.assertNotNull(validRE3);
        Assertions.assertEquals(ValidRequestException.class, validRE3.getClass());
        Assertions.assertEquals("Введен неверный пароль", validRE3.getMessage());

        Mockito.when(passwordEncoder.matches(dto.getPin(), accountFrom.getPin())).thenReturn(true);
        Throwable validRE4 = Assertions.assertThrows(ValidRequestException.class,
                () -> accountService.transferAccount(dto));
        Assertions.assertNotNull(validRE4);
        Assertions.assertEquals(ValidRequestException.class, validRE4.getClass());
        Assertions.assertEquals("На счете недостаточно средств", validRE4.getMessage());

        dto.setAmount(accountFromAmount);
        Map<String, Account> accountMap1 = accountService.transferAccount(dto);
        Mockito.verify(accountRepository, Mockito.times(2)).save(Mockito.any(Account.class));
        Assertions.assertEquals(accountMap1.get("AccountFrom").getId(), accountFrom.getId());
        Assertions.assertEquals(accountMap1.get("AccountFrom").getAmount(), accountFromAmount.subtract(dto.getAmount()));
        Assertions.assertEquals(accountMap1.get("AccountTo").getId(), accountTo.getId());
        Assertions.assertEquals(accountMap1.get("AccountTo").getAmount(), accountToAmount.add(dto.getAmount()));

        accountFrom.setAmount(accountFromAmount);
        accountTo.setAmount(accountToAmount);
        dto.setAmount(accountFromAmount.subtract(BigDecimal.valueOf(0.01)));
        Map<String, Account> accountMap2 = accountService.transferAccount(dto);
        Mockito.verify(accountRepository, Mockito.times(4)).save(Mockito.any(Account.class));
        Assertions.assertEquals(accountMap2.get("AccountFrom").getId(), accountFrom.getId());
        Assertions.assertEquals(accountMap2.get("AccountFrom").getAmount(), accountFromAmount.subtract(dto.getAmount()));
        Assertions.assertEquals(accountMap2.get("AccountTo").getId(), accountTo.getId());
        Assertions.assertEquals(accountMap2.get("AccountTo").getAmount(), accountToAmount.add(dto.getAmount()));

    }
}