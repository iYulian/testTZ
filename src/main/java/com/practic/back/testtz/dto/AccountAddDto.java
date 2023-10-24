package com.practic.back.testtz.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountAddDto {
    @NotNull(message = "Имя должно быть указанно")
    private String beneficiaryName;
    @NotNull(message = "Должен присутствовать пин-код из 4х цифр")
    @Pattern(regexp = "^[0-9]{4}$", message = "Должен присутствовать пин-код из 4х цифр")
    private String pin;
}
