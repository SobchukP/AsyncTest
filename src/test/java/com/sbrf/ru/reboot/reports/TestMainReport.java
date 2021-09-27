package com.sbrf.ru.reboot.reports;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sbrf.reboot.Currency;
import com.sbrf.reboot.dto.Account;
import com.sbrf.reboot.dto.Customer;
import com.sbrf.reboot.reports.MainReport;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestMainReport {

    List<Customer> customers = new ArrayList<>() {{
        add(new Customer(25, "Ivan", new Account(new BigDecimal(1000), Currency.USD, LocalDate.of(2021, 7, 8))));
        add(new Customer(31, "Petr", new Account(new BigDecimal(2000), Currency.RUR, LocalDate.of(2021, 7, 8))));
        add(new Customer(25, "Sergey", new Account(new BigDecimal(500), Currency.EUR, LocalDate.of(2021, 7, 8))));
        add(new Customer(20, "Denis", new Account(new BigDecimal(3000), Currency.RUR, LocalDate.of(2021, 7, 8))));
        add(new Customer(29, "Pavel", new Account(new BigDecimal(4000), Currency.RUR, LocalDate.of(2021, 9, 8))));
    }};

    MainReport report = new MainReport(18, 30, LocalDate.of(2021, 7, 1), LocalDate.of(2021, 8, 1));

    @Test
    void getTotalsWithCompletableFuture(){
       Assertions.assertTrue(report.getTotalsWithCompletableFuture(customers.stream()) instanceof BigDecimal);
       Assertions.assertTrue(new BigDecimal(3000).equals(report.getTotalsWithCompletableFuture(customers.stream())));

    }

    @Test
    void getTotalsWithReact(){
        Assertions.assertTrue(new BigDecimal(3000).equals(report.getTotalsWithCompletableFuture(customers.stream())));
    }
}
