package com.catalog.spring.repositories;

import com.catalog.spring.model.ProductLike;
import com.catalog.spring.model.ProductLikeId;
import com.catalog.spring.model.Account;
import com.catalog.spring.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface ProductLikeRepository extends JpaRepository<ProductLike, ProductLikeId> {
    List<ProductLike> findByAccount(Account account);
    List<ProductLike> findByProduct(Product product);
    boolean existsByAccountAndProduct(Account account, Product product);
    long countByProduct(Product product);
}


