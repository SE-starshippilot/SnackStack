package com.snackstack.server.model;

import java.time.Instant;

public record Recipe(
    Integer recipeId,
    String recipeName,
    String description,
    Integer servings,
    String recipeOriginId,
    String recipeType
) {

}