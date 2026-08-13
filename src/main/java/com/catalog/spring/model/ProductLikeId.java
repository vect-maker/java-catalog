package com.catalog.spring.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(staticName = "of")
public class ProductLikeId implements Serializable {

    @Column(name = "account_id")
    private UUID accountId;

    @Column(name = "product_id")
    private UUID productId;
}