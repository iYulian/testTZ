package com.practic.back.testtz.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class WithdrawDto {
    @NotNull(message = "Необходимо указать с какого счета снимать сумму")
    private UUID accountIdFrom;

    @DecimalMin(value = "0.01", message = "Сумма перевода не может быть отрицательной или нулевой")
    private BigDecimal amount;
    @NotNull(message = "Должен присутствовать пин-код из 4х цифр")
    @Pattern(regexp = "^[0-9]{4}$", message = "Должен присутствовать пин-код из 4х цифр")
    private String pin;
}
