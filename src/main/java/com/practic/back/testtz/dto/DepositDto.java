package com.practic.back.testtz.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class DepositDto {
    @NotNull(message = "Необходимо указать на какой счет переводить сумму")
    private UUID accountIdTo;
    @DecimalMin(value = "0.01", message = "Сумма перевода не может быть отрицательной или нулевой")
    private BigDecimal amount;
}
