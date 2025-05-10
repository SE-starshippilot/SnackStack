package com.snackstack.server.model;

import java.time.Instant;

public record RecipeHistory(
    Integer historyId,
    Integer userId,
    Integer recipeId,
    Instant createdAt
) {

}
