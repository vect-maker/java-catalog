package com.catalog.spring.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    String title,
    String description,
    BigDecimal price,
    Instant createdAt,
    UUID publishedById,
    String publishedByUsername,
    Set<UUID> categoryIds,
    long likeCount
) {}