package com.snackstack.server.model;

import java.util.List;


public record Recipe(
    int id,
    String recipeName,
    String description,
    int servings,
    String recipeOrigin,
    String recipeType
) {

}
