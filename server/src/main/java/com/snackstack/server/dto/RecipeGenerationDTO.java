package com.snackstack.server.dto;

import com.snackstack.server.model.RecipeType;
import java.util.List;

public record RecipeGenerationDTO(
    List<String> availableIngredients,
    int servings,
    RecipeType recipeType,
    List<String> mealOrigin,
    List<String> allergies
) {

}
