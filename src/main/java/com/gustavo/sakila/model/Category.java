package com.gustavo.sakila.model;

import java.time.Instant;

public record Category(int categoryId, String name, Instant lastUpdate) {}