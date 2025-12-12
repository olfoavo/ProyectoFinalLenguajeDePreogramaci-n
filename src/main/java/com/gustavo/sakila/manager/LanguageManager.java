package com.gustavo.sakila.manager;

import com.gustavo.sakila.model.Language;
import com.gustavo.sakila.repo.LanguageRepository;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.Instant;

public class LanguageManager extends BaseManager<Language> {

    private final LanguageRepository languageRepo;

    public LanguageManager(DataSource ds) {
        super(new LanguageRepository(ds));
        this.languageRepo = (LanguageRepository) this.repo;
    }

    public int create(String name) {
        try {
            return languageRepo.insert(new Language(0, name, Instant.now()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean rename(int id, String newName) {
        try {
            return languageRepo.rename(id, newName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(int id) {
        try {
            return languageRepo.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}