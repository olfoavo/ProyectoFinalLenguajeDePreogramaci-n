package com.gustavo.sakila.model;

import java.time.Instant;

public record Actor(int actorId, String firstName, String lastName, Instant lastUpdate) {}