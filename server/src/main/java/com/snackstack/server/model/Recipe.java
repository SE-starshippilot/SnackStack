package com.snackstack.server.model;

public record Recipe(
    int id,
    String recipeName,
    String description,
    Integer servings,
    String recipeOrigin,
    String recipeType,
    Boolean isFavorite,
    String uuid
) {

}
