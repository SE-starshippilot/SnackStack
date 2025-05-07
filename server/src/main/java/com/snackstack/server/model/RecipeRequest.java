package com.snackstack.server.model;

import java.util.List;

public record RecipeRequest(
    int servingSize,
    String mealType,
    List<String> preference,
    List<String> allergies
) {

}