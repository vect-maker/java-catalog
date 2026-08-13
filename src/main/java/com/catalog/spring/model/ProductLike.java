package com.catalog.spring.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_likes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductLike {

    @EmbeddedId
    private ProductLikeId id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("accountId")
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public ProductLike(Account account, Product product) {
        if (account.getId() == null || product.getId() == null) {
            throw new IllegalStateException("Account and Product must be persisted before creating a like");
        }
        this.id = ProductLikeId.of(account.getId(), product.getId());
        this.account = account;
        this.product = product;
    }
}