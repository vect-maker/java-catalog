package com.catalog.spring.controllers;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.catalog.spring.dto.LikeResponse;
import com.catalog.spring.dto.ProductRequest;
import com.catalog.spring.dto.ProductResponse;
import com.catalog.spring.model.Account;
import com.catalog.spring.model.Category;
import com.catalog.spring.model.Product;
import com.catalog.spring.model.ProductLike;
import com.catalog.spring.model.ProductLikeId;
import com.catalog.spring.repositories.AccountRepository;
import com.catalog.spring.repositories.CategoryRepository;
import com.catalog.spring.repositories.ProductLikeRepository;
import com.catalog.spring.repositories.ProductRepository;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final ProductLikeRepository productLikeRepository;

    public ProductController(ProductRepository productRepository,
            AccountRepository accountRepository,
            CategoryRepository categoryRepository,
            ProductLikeRepository productLikeRepository) {
        this.productRepository = productRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.productLikeRepository = productLikeRepository;
    }

    // CRUD

    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest request) {
        Account account = currentAccount();

        Set<Category> categories = resolveCategories(request.categoryIds());

        Product product = new Product(
                request.title(),
                request.description(),
                request.price(),
                account);
        product.setCategories(categories);

        Product saved = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> list(Pageable pageable) {
        Page<ProductResponse> page = productRepository.findAll(pageable).map(this::toResponse);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return ResponseEntity.ok(toResponse(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id, @RequestBody ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!isOwner(product)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (request.title() != null)
            product.setTitle(request.title());
        if (request.description() != null)
            product.setDescription(request.description());
        if (request.price() != null)
            product.setPrice(request.price());
        if (request.categoryIds() != null)
            product.setCategories(resolveCategories(request.categoryIds()));

        Product updated = productRepository.save(product);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!isOwner(product)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        product.softDelete();
        productRepository.save(product);
        return ResponseEntity.noContent().build();
    }

    // Likes

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> like(@PathVariable UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Account account = currentAccount();

        if (productLikeRepository.existsByAccountAndProduct(account, product)) {
            return ResponseEntity.badRequest().build(); 
        }

        productLikeRepository.save(new ProductLike(account, product));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlike(@PathVariable UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Account account = currentAccount();

        ProductLikeId likeId = ProductLikeId.of(account.getId(), product.getId());
        if (!productLikeRepository.existsById(likeId)) {
            return ResponseEntity.notFound().build();
        }

        productLikeRepository.deleteById(likeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/likes")
    public ResponseEntity<LikeResponse> getLikes(@PathVariable UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Account account = currentAccount();

        long count = productLikeRepository.countByProduct(product);
        boolean liked = productLikeRepository.existsByAccountAndProduct(account, product);
        return ResponseEntity.ok(new LikeResponse(count, liked));
    }

    // helpers

    private Account currentAccount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private boolean isOwner(Product product) {
        return product.getPublishedBy().getId().equals(currentAccount().getId());
    }

    private Set<Category> resolveCategories(Set<UUID> ids) {
        if (ids == null || ids.isEmpty())
            return Set.of();
        return StreamSupport.stream(categoryRepository.findAllById(ids).spliterator(), false)
                .collect(Collectors.toSet());
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getTitle(),
                product.getDescription(),
                product.getPrice(),
                product.getCreatedAt(),
                product.getPublishedBy().getId(),
                product.getPublishedBy().getUsername(),
                product.getCategories().stream()
                        .filter(Objects::nonNull)
                        .map(category -> category.getId())
                        .collect(Collectors.toSet()),
                productLikeRepository.countByProduct(product));
    }
}