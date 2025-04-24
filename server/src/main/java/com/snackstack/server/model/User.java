package com.snackstack.server.model;

import java.time.Instant;
public record User(
    Integer userId,
    String userName,
    String email,
    Instant createdAt,
    Instant lastLoginAt) {}
