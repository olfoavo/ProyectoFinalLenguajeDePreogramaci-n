package com.gustavo.sakila.model;

import java.time.Instant;

public record FilmActor(int actorId, int filmId, Instant lastUpdate) {}