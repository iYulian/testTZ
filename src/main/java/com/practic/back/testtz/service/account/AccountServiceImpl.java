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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService{
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public List<AccountInfDto> getAccounts() {
        List<Account> accounts = accountRepository.findAll();
        if (accounts.isEmpty()){
            log.error("Нет возможности показать аккаунты");
            throw new NotFoundRequestException("Нет ни одного счета");
        }
        return accounts.stream().map(x -> AccountInfDto.builder()
                .amount(x.getAmount()).beneficiaryName(x.getBeneficiaryName()).build()).toList();
    }

    @Override
    @Transactional
    public Account addAccount(AccountAddDto accountDto) {
        Account account = new Account();
        account.setBeneficiaryName(accountDto.getBeneficiaryName());
        account.setPin(passwordEncoder.encode(accountDto.getPin()));
        account.setAmount(BigDecimal.valueOf(0));
        accountRepository.save(account);
        log.info("Счет на имя {} успешно добавлен", accountDto.getBeneficiaryName());
        return account;
    }

    @Override
    @Transactional
    public Account depositAccount(DepositDto dto) {
        accountExistsCheck(dto.getAccountIdTo());
        Account account = accountRepository.getReferenceById(dto.getAccountIdTo());
        account.setAmount(account.getAmount().add(dto.getAmount()));
        accountRepository.save(account);
        log.info("Счет {} успешно пополнен на {}", dto.getAccountIdTo(), dto.getAmount());
        return account;
    }

    @Override
    @Transactional
    public Account withdrawAccount(WithdrawDto dto) {
        accountExistsCheck(dto.getAccountIdFrom());
        Account accountFrom = accountRepository.getReferenceById(dto.getAccountIdFrom());
        withdrawCheck(accountFrom, dto.getPin(), dto.getAmount());
        accountFrom.setAmount(accountFrom.getAmount().subtract(dto.getAmount()));
        accountRepository.save(accountFrom);
        log.info("Со счета {} успешно выведено {}", dto.getAccountIdFrom(), dto.getAmount());
        return accountFrom;
    }

    @Override
    @Transactional
    public Map<String, Account> transferAccount(TransferDto dto) {
        accountExistsCheck(dto.getAccountIdTo());
        accountExistsCheck(dto.getAccountIdFrom());
        if (dto.getAccountIdFrom().equals(dto.getAccountIdTo())){
            log.error("С банковского счета не могут быть сняты средства");
            throw new ValidRequestException("Счет получатель и счет отправитель одинаковые");
        }
        Account accountFrom = accountRepository.getReferenceById(dto.getAccountIdFrom());
        withdrawCheck(accountFrom, dto.getPin(), dto.getAmount());
        Account accountTo = accountRepository.getReferenceById(dto.getAccountIdTo());
        accountFrom.setAmount(accountFrom.getAmount().subtract(dto.getAmount()));
        accountTo.setAmount(accountTo.getAmount().add(dto.getAmount()));
        accountRepository.save(accountFrom);
        accountRepository.save(accountTo);
        Map<String, Account> accountMap = new HashMap<>();
        accountMap.put("AccountFrom", accountFrom);
        accountMap.put("AccountTo", accountTo);
        log.info("Со счета {} успешно переведено {} на счет {}",
                dto.getAccountIdFrom(), dto.getAmount(), dto.getAccountIdTo());
        return accountMap;
    }

    private void withdrawCheck(Account accountFrom, String pin, BigDecimal amount) throws ValidRequestException{
        if (!passwordEncoder.matches(pin, accountFrom.getPin())) {
            log.error("С банковского счета не могут быть сняты средства");
            throw new ValidRequestException("Введен неверный пароль");
        }
        if (accountFrom.getAmount().subtract(amount).compareTo(BigDecimal.valueOf(0)) < 0){
            log.error("С банковского счета не могут быть сняты средства");
            throw new ValidRequestException("На счете недостаточно средств");
        }
    }
    private void accountExistsCheck(UUID id) {
        if (!accountRepository.existsById(id)) {
            log.error("С банковского счета не могут быть сняты средства");
            throw new ValidRequestException("Не существует банковского счета с id - " + id);
        }
    }
}
