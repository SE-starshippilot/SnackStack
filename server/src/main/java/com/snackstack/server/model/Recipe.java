package com.snackstack.server.model;



public record Recipe(
    int id,
    String recipeName,
    String description,
    int servings,
    String recipeOrigin,
    String recipeType,
    boolean is_favorite
) {

}
