package com.snackstack.server.model;

public record RecipeIngredient(
    int recipeId,
    int ingredientId,
    float quantity,
    String unit,
    String note
) {

}
