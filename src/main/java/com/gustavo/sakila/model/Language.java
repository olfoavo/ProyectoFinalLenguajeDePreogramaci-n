package com.gustavo.sakila.model;

import java.time.Instant;

public record Language(int languageId, String name, Instant lastUpdate) {}