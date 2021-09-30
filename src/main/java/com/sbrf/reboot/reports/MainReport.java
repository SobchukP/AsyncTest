package com.sbrf.reboot.reports;

import com.sbrf.reboot.Currency;
import reactor.core.publisher.Flux;

import com.sbrf.reboot.dto.Account;
import com.sbrf.reboot.dto.Customer;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
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
        Stream<Stream<Customer>> streams = splitStreams(customers);
        List<CompletableFuture<BigDecimal>> futuresList = streams
                .map(elem -> getCompletableFuture(elem))
                .collect(Collectors.toList());

        CompletableFuture<BigDecimal> allFutures = CompletableFuture.allOf(futuresList.toArray(new CompletableFuture[0]))
                .thenApply(elem -> futuresList.stream()
                        .map(item -> item.join())
                        .reduce((el1, el2) -> el1.add(el2)).orElse(BigDecimal.ZERO)
                );

        return allFutures.join();
    }

    public BigDecimal getTotalsWithReact(Stream<Customer> customers){
        BigDecimal resultSum = BigDecimal.ZERO;
        Stream<Flux<Account>> fluxStream = splitStreams(customers)
                .map(acc -> getFlux(acc, Schedulers.newParallel("Scheduler"+new Random().nextInt())));
        fluxStream.reduce(Flux.empty(), Flux::merge).subscribe(it -> resultSum.add(it.getSum()));
        return resultSum;
    }

    private CompletableFuture<BigDecimal> getCompletableFuture(Stream<Customer> customers){
        return CompletableFuture.supplyAsync(() -> sampleAccountsByAge(customers))
                .thenApplyAsync(this::sampleAccountByCreateDate)
                .thenApplyAsync(this::sampleAccountByCurrency)
                .thenApplyAsync(this::calculationBalanceSum);
    }

    private Flux<Account> getFlux(Stream<Customer> customers, Scheduler scheduler){
        return Flux.fromStream(sampleAccountsByAge(customers)).publishOn(scheduler).log()
                .filter(acc -> acc.getCreateDate().isAfter(dateStart) && acc.getCreateDate().isBefore(dateEnd))
                .filter(acc -> acc.getCurrency() == Currency.RUR);
    }

    private Stream<Stream<Customer>> splitStreams (Stream<Customer> customers){
        int processorsCount = Runtime.getRuntime().availableProcessors();
        Map<Integer, List<Customer>> groupStreams = customers.collect(Collectors.groupingBy(elem -> new Random().nextInt(processorsCount)));
        return groupStreams.values().stream().map(elem -> elem.stream());
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
        return accounts.map(elem -> elem.getSum()).reduce((el1, el2) -> el1.add(el2)).orElse(BigDecimal.ZERO);
    }

}
