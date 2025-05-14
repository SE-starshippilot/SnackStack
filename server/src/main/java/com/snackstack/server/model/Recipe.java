package com.snackstack.server.model;

public record Recipe(
    Integer recipeId,
    String recipeName,
    String description,
    Integer servings,
    String recipeOrigin,
    RecipeType recipeType,
    String uuid
) {

}