package com.practic.back.testtz.controller;

import com.practic.back.testtz.dto.AccountAddDto;
import com.practic.back.testtz.dto.AccountInfDto;
import com.practic.back.testtz.dto.DepositDto;
import com.practic.back.testtz.dto.TransferDto;
import com.practic.back.testtz.dto.WithdrawDto;
import com.practic.back.testtz.entity.AccountAudit;
import com.practic.back.testtz.exception.ValidRequestException;
import com.practic.back.testtz.service.account.AccountService;
import com.practic.back.testtz.service.accountAudit.AccountAuditService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@OpenAPIDefinition(
        info = @Info(
                title = "Тестовое Rest API",
                description = "Тестовое Rest API для создания банковских счетов и переводов между ними",
                contact = @Contact(name = "Yulian", email = "email@email.ru"),
                version = "0.1.0"
        ),
        servers = @Server(
                description = "Сервер Transfer'а",
                url = "http://localhost:8080"
        )

)
@RestController
@AllArgsConstructor
@Slf4j(topic = "AccountController")
public class AccountController {
    private final AccountService accountService;
    private final AccountAuditService accountAuditService;
    @Operation(
            summary = "Запрос списка всех счетов",
            description = "Выводит список всех счетов"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping("/accounts")
    public List<AccountInfDto> getAccounts(){
        log.info("Принят запрос на получение информации о счетах");
        return accountService.getAccounts();
    }
    @Operation(
            summary = "Запрос списка всех операций со счетом",
            description = "Выводит список всех операций со счетом"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Объект не найден"),
            @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping("/accounts/transactions/{id}")
    public List<AccountAudit> getTransactions(@PathVariable(name = "id") UUID accountId){
        log.info("Принят запрос на получение информации о истории изменения счета");
        return accountAuditService.getAccountAuditByAccountId(accountId);
    }
    @Operation(
            summary = "Запрос списка всех счетов",
            description = "Выводит список всех счетов"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные о создаваемом счете",
            required = true,
            content = @Content(schema = @Schema(implementation = AccountAddDto.class))

    )
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Переданные неверные данные"),
            @ApiResponse(responseCode = "404", description = "Объект не найден"),
            @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PostMapping("/accounts")
    public void createAccount(@RequestBody @Valid AccountAddDto account, BindingResult bindingResult){
        log.info("Принят запрос на создание счета");
        if (bindingResult.hasErrors()){
            log.error("Перевод не удался. Переданы некорректные данные");
            throw new ValidRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        accountService.addAccount(account);
    }
    @Operation(
            summary = "Запрос списка всех счетов",
            description = "Выводит список всех счетов"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные о депозите",
            required = true,
            content = @Content(schema = @Schema(implementation = DepositDto.class))

    )
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Переданные неверные данные"),
            @ApiResponse(responseCode = "404", description = "Объект не найден"),
            @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PutMapping("/accounts/deposit")
    public void depositAccount(@RequestBody @Valid DepositDto depositDto, BindingResult bindingResult) {
        log.info("Принят запрос на ввод средств на счет");
        if (bindingResult.hasErrors()){
            log.error("Перевод не удался. Переданы некорректные данные");
            throw new ValidRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        accountService.depositAccount(depositDto);
    }
    @Operation(
            summary = "Запрос на списание средств со счета",
            description = "Списывает со счета средства"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные о снятии",
            required = true,
            content = @Content(schema = @Schema(implementation = WithdrawDto.class))

    )
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Переданные неверные данные"),
            @ApiResponse(responseCode = "404", description = "Объект не найден"),
            @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PutMapping("/accounts/withdraw")
    public void withdrawAccount(@RequestBody @Valid WithdrawDto withdrawDto, BindingResult bindingResult) {
        log.info("Принят запрос на снятие средств со счета");
        if (bindingResult.hasErrors()){
            log.error("Перевод не удался. Переданы некорректные данные");
            throw new ValidRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        accountService.withdrawAccount(withdrawDto);
    }
    @Operation(
            summary = "Запрос перевод средств с одного аккаунта на другой",
            description = "Переводит средства с одного аккаунта на другой"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Данные о переводе",
            required = true,
            content = @Content(schema = @Schema(implementation = TransferDto.class))

    )
    @ApiResponses({
            @ApiResponse(responseCode = "400", description = "Переданные неверные данные"),
            @ApiResponse(responseCode = "404", description = "Объект не найден"),
            @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно"),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @PutMapping("/accounts/transfer")
    public void transferAccount(@RequestBody @Valid TransferDto transfer, BindingResult bindingResult) {
        log.info("Принят запрос на перевод с счета {} на {}", transfer.getAccountIdFrom(), transfer.getAccountIdTo());
        if (bindingResult.hasErrors()){
            log.error("Перевод не удался. Переданы некорректные данные");
            throw new ValidRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        accountService.transferAccount(transfer);
    }
}
