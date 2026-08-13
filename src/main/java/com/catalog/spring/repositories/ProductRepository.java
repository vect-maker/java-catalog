package com.catalog.spring.repositories;

import com.catalog.spring.model.Product;
import com.catalog.spring.model.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findByPublishedBy(Account publishedBy);

    List<Product> findByPublishedById(UUID accountId);

    @Override
    Optional<Product> findById(UUID id);

    @Override
    Page<Product> findAll(Pageable pageable);
}