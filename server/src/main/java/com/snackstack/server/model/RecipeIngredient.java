package com.snackstack.server.model;

public record RecipeIngredient(
    Integer recipeId,
    Integer ingredientId,
    Float quantity,
    String unit,
    String note
) {

}
