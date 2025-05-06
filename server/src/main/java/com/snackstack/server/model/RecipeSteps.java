package com.snackstack.server.model;

public record RecipeSteps(
    Integer stepId,
    Integer recipeId,
    Integer stepNumber,
    String stepDescription
) {

}
