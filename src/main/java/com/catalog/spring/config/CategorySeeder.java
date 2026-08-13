package com.catalog.spring.config;

import com.catalog.spring.model.Category;
import com.catalog.spring.repositories.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategorySeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public CategorySeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) return;

        categoryRepository.save(new Category("Electronics", "Gadgets and devices"));
        categoryRepository.save(new Category("Clothing", "Apparel and accessories"));
        categoryRepository.save(new Category("Home & Garden", "Furniture, decor, and gardening"));
        categoryRepository.save(new Category("Phones", "Smartphones and accessories"));
        categoryRepository.save(new Category("Laptops", "Notebooks and workstations"));
        categoryRepository.save(new Category("Shirts", "T-shirts, polos, and dress shirts"));
    }
}