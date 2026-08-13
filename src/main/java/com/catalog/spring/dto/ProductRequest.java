package com.catalog.spring.dto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record ProductRequest(String title, String description, BigDecimal price, Set<UUID> categoryIds) {}