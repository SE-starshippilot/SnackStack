package com.snackstack.server.dto;

import java.util.List;

public record RecipeRequestDTO(
    List<String> ingredients,
    int servings,
    String meal_type,
    List<String> meal_origin,
    List<String> allergies
) {

}