package com.catalog.spring.dto;

import java.util.UUID;

public record CategoryRequest(String name, String description, UUID parentId) {}