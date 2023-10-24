package com.practic.back.testtz.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@Builder
public class AccountInfDto {
    private String beneficiaryName;
    private BigDecimal amount;
}
