package com.snackstack.server.dto;

import com.snackstack.server.model.RecipeType;
import java.util.List;

public record RecipeRequestDTO(
    int servings,
    RecipeType recipeType,
    List<String> recipeOrigin,
    List<String> allergies
) {

}
