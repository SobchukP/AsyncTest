package com.sbrf.reboot;

import com.sbrf.reboot.dto.Account;
import com.sbrf.reboot.dto.Customer;
import com.sbrf.reboot.reports.MainReport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Customer> customers = new ArrayList<>() {{
            add(new Customer(25, "Ivan", new Account(new BigDecimal(1000), Currency.USD, LocalDate.of(2021, 7, 8))));
            add(new Customer(31, "Petr", new Account(new BigDecimal(2000), Currency.RUR, LocalDate.of(2021, 7, 8))));
            add(new Customer(25, "Sergey", new Account(new BigDecimal(500), Currency.EUR, LocalDate.of(2021, 7, 8))));
            add(new Customer(20, "Denis", new Account(new BigDecimal(3000), Currency.RUR, LocalDate.of(2021, 7, 8))));
            add(new Customer(29, "Pavel", new Account(new BigDecimal(4000), Currency.RUR, LocalDate.of(2021, 9, 8))));
        }};
        //customers.forEach(item -> System.out.println(item));
        customers.forEach(customer -> System.out.println(customer));
        MainReport report = new MainReport(18, 30, LocalDate.of(2021, 7, 1), LocalDate.of(2021, 8, 1));

        System.out.println(report.getTotalsWithCompletableFuture(customers.stream()));
    }
}
