package com.sbrf.reboot.dto;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class Customer {
    private int age;
    private String name;
    private Set<Account> accounts;

    public Customer (int age, String name, Account account){
        this.age = age;
        this.name = name;
        this.accounts = new HashSet<>();
        accounts.add(account);
    }

    public void openNewAccount(Account account){
        accounts.add(account);
    }
}
