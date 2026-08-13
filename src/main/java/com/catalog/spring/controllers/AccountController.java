package com.catalog.spring.controllers;

import com.catalog.spring.dto.AccountUpdateRequest;
import com.catalog.spring.dto.LoginRequest;
import com.catalog.spring.dto.RegisterRequest;
import com.catalog.spring.model.Account;
import com.catalog.spring.repositories.AccountRepository;
import com.catalog.spring.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AccountController {

    private final AuthenticationManager authenticationManager;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AccountController(AuthenticationManager authenticationManager,
            AccountRepository accountRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(account.getEmail(), account.getId());

        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {
        if (accountRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body("Email already in use");
        }
        if (accountRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().body("Username already in use");
        }

        Account account = new Account(
                request.email(),
                request.username(),
                passwordEncoder.encode(request.password()));

        accountRepository.save(account);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/me")
    public ResponseEntity<Object> update(@RequestBody AccountUpdateRequest request) {
        Account account = currentAccount();

        if (request.username() != null && !request.username().equals(account.getUsername())) {
            if (accountRepository.existsByUsername(request.username())) {
                return ResponseEntity.badRequest().body("Username already in use");
            }
            account.setUsername(request.username());
        }

        if (request.password() != null) {
            account.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        accountRepository.save(account);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> delete() {
        Account account = currentAccount();
        account.softDelete();
        accountRepository.save(account);
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }

    private Account currentAccount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}