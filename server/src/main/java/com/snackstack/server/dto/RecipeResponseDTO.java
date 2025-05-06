package com.snackstack.server.dto;

import java.util.List;

public record RecipeResponseDTO(
    String recipe_name,
    int servings,
    String description,
    String origin_name,
    List<IngredientDTO> recipe_ingredients,
    List<String> recipe_steps
) {

}
