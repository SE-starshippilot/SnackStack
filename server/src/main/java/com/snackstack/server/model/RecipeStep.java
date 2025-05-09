package com.snackstack.server.model;

public record RecipeStep(
    int recipeId,
    int stepNumber,
    String stepDescription
) {

}
