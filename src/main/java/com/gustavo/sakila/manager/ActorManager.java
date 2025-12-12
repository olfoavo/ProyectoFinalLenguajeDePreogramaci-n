package com.gustavo.sakila.manager;

import com.gustavo.sakila.model.Actor;
import com.gustavo.sakila.repo.ActorRepository;

import javax.sql.DataSource;
import java.time.Instant;

public class ActorManager extends BaseManager<Actor> {
    private final ActorRepository actorRepo;

    public ActorManager(DataSource ds) {
        super(new ActorRepository(ds));
        this.actorRepo = (ActorRepository) this.repo;
    }

    public int create(String first, String last) {
        try {
            return actorRepo.insert(new Actor(0, first, last, Instant.now()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}