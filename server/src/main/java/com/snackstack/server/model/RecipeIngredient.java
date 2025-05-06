package com.snackstack.server.model;

public record RecipeIngredient(
    Integer recipeId,
    Integer ingredientId,
    Integer quantity,
    String unit,
    String note
) {

}
