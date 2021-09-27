package com.sbrf.reboot.dto;

import com.sbrf.reboot.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class Account {
    private BigDecimal sum;
    private Currency currency;
    private LocalDate createDate;

    public Account(BigDecimal sum, Currency currency, LocalDate createDate){
        this.createDate =createDate;
        this.currency = currency;
        this.sum = sum;
    }
}
