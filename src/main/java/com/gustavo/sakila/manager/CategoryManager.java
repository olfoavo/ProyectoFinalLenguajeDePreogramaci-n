package com.gustavo.sakila.manager;

import com.gustavo.sakila.model.Category;
import com.gustavo.sakila.repo.CategoryRepository;

import javax.sql.DataSource;
import java.time.Instant;

public class CategoryManager extends BaseManager<Category> {
    public CategoryManager(DataSource ds) {
        super(new CategoryRepository(ds));
    }

    public int create(String name) {
        try {
            return ((CategoryRepository) repo).insert(new Category(0, name, Instant.now()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}