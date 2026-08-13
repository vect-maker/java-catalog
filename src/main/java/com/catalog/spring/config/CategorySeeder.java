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

        Category electronics = new Category("Electronics", "Gadgets and devices");
        Category clothing = new Category("Clothing", "Apparel and accessories");
        Category home = new Category("Home & Garden", "Furniture, decor, and gardening");

        categoryRepository.save(electronics);
        categoryRepository.save(clothing);
        categoryRepository.save(home);

        // Sub-categories
        Category phones = new Category("Phones", "Smartphones and accessories");
        phones.setParent(electronics);

        Category laptops = new Category("Laptops", "Notebooks and workstations");
        laptops.setParent(electronics);

        Category shirts = new Category("Shirts", "T-shirts, polos, and dress shirts");
        shirts.setParent(clothing);

        categoryRepository.save(phones);
        categoryRepository.save(laptops);
        categoryRepository.save(shirts);
    }
}