package com.sbrf.reboot.reports;

import com.sbrf.reboot.Currency;
import reactor.core.publisher.Flux;

import com.sbrf.reboot.dto.Account;
import com.sbrf.reboot.dto.Customer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;


public class MainReport {
    private final int minAge;
    private final int maxAge;
    private final LocalDate dateStart;
    private final LocalDate dateEnd;

    public  MainReport (int minAge, int maxAge, LocalDate dateStart, LocalDate dateEnd){
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.dateEnd = dateEnd;
        this.dateStart = dateStart;
    }

    public BigDecimal getTotalsWithCompletableFuture (Stream<Customer> customers) {
        CompletableFuture<BigDecimal> future = CompletableFuture.supplyAsync(() -> sampleAccountsByAge(customers))
                .thenApply(this::sampleAccountByCreateDate)
                .thenApply(this::sampleAccountByCurrency)
                .thenApply(this::calculationBalanceSum);
        return future.join();
    }

    public BigDecimal getTotalsWithReact(Stream<Customer> customers){
        final BigDecimal resultSum = new BigDecimal(0);
        Flux<Account> flux = Flux.fromStream(sampleAccountsByAge(customers))
                .filter(acc -> acc.getCreateDate().isAfter(dateStart) && acc.getCreateDate().isBefore(dateEnd))
                .filter(elem -> elem.getCurrency() == Currency.RUR);
        flux.subscribe(it -> resultSum.add(it.getSum()));
        return resultSum;
    }

    private Stream<Account> sampleAccountsByAge(Stream<Customer> customers) {
        return customers.filter(acc -> acc.getAge() >= minAge && acc.getAge() <= maxAge)
                .map(acc -> acc.getAccounts())
                .flatMap(sum -> sum.stream());
    }

    private Stream<Account> sampleAccountByCreateDate(Stream<Account> accounts){
        return accounts.filter(acc -> acc.getCreateDate().isAfter(dateStart) && acc.getCreateDate().isBefore(dateEnd));
    }

    private Stream<Account> sampleAccountByCurrency(Stream<Account> accounts){
        return accounts.filter(elem -> elem.getCurrency() == Currency.RUR);
    }

    private BigDecimal calculationBalanceSum(Stream<Account> accounts){
        return accounts.map(elem -> elem.getSum()).reduce((el1, el2) -> el1.add(el2)).orElseThrow(IllegalArgumentException::new);
    }

}
