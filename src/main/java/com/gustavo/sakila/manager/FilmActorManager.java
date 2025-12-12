package com.gustavo.sakila.manager;

import com.gustavo.sakila.repo.FilmActorRepository;

import javax.sql.DataSource;

public class FilmActorManager {
    private final FilmActorRepository repo;

    public FilmActorManager(DataSource ds) {
        this.repo = new FilmActorRepository(ds);
    }

    public boolean link(int filmId, int actorId) {
        try { return repo.addLink(filmId, actorId); }
        catch (Exception e) { throw new RuntimeException(e); }
    }

    public boolean unlink(int filmId, int actorId) {
        try { return repo.removeLink(filmId, actorId); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}