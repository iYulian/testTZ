package com.practic.back.testtz.service.account;

import com.practic.back.testtz.dto.AccountAddDto;
import com.practic.back.testtz.dto.AccountInfDto;
import com.practic.back.testtz.dto.DepositDto;
import com.practic.back.testtz.dto.TransferDto;
import com.practic.back.testtz.dto.WithdrawDto;
import com.practic.back.testtz.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AccountService {
    List<AccountInfDto> getAccounts();
    Account addAccount(AccountAddDto account);
    Account depositAccount(DepositDto dto);
    Account withdrawAccount(WithdrawDto dto);
    Map<String, Account> transferAccount(TransferDto dto);
}
