package com.catalog.spring.config;

import com.catalog.spring.model.Account;
import com.catalog.spring.model.Role;
import com.catalog.spring.repositories.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AccountSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountSeeder(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (accountRepository.existsByEmail("admin@catalog.local")) {
            return;
        }

        Account admin = new Account(
                "admin@catalog.local",
                "admin",
                passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);

        accountRepository.save(admin);
    }
}