package com.snackstack.server.dto;

import java.util.List;
import java.util.UUID;

public record RecipeResponseDTO(
    String uuid,
    String recipeName,
    int servings,
    String description,
    String originName,
    List<IngredientDTO> recipeIngredients,
    List<String> recipeSteps
) {
  public RecipeResponseDTO(
      String recipe_name,
      int servings,
      String description,
      String origin_name,
      List<IngredientDTO> recipe_ingredients,
      List<String> recipe_steps) {
    this(UUID.randomUUID().toString(), recipe_name, servings, description,
        origin_name, recipe_ingredients, recipe_steps);
  }
}
