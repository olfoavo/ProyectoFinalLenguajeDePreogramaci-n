package com.gustavo.sakila.model;

import java.time.Instant;

public record Film(int filmId, String title, String description, int languageId, Instant lastUpdate) {}