package com.gustavo.sakila.manager;

import com.gustavo.sakila.model.Film;
import com.gustavo.sakila.repo.FilmRepository;

import javax.sql.DataSource;
import java.time.Instant;

public class FilmManager extends BaseManager<Film> {
    public FilmManager(DataSource ds) {
        super(new FilmRepository(ds));
    }

    public int create(String title, String description, int languageId) {
        try {
            return ((FilmRepository) repo).insert(new Film(0, title, description, languageId, Instant.now()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}