package com.example.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.exception.UsernameNotFoundException;
import com.example.repository.AccountRepository;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;

    AccountService(AccountRepository newAccountRepository){
        this.accountRepository = newAccountRepository;
    }

    public void register(Account newAccount){
        this.accountRepository.save(newAccount);
    }

    public Account login(Account loginAccount){
        Optional<Account> foundAccount = accountRepository.findByUsernameAndPassword(loginAccount.getUsername(), loginAccount.getPassword());
        return foundAccount.orElse(null);
    }

    public Account getAccountByUsername(String username) throws UsernameNotFoundException{
        Optional<Account> foundAccount = accountRepository.findByUsername(username);
        return foundAccount.orElse(null);
    }

    public Account getAccountById(int accountId){
        Optional<Account> foundAccount = accountRepository.findByaccountId(accountId);
        return foundAccount.orElse(null);
    }
}
