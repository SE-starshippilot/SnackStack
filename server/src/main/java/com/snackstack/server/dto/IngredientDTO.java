package com.snackstack.server.dto;

public record IngredientDTO(
    String ingredient_name,
    float quantity,
    String unit,
    String note
) {

}
