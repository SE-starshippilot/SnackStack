package com.snackstack.server.dto;

public record IngredientDTO(
    String ingredientName,
    float quantity,
    String unit,
    String note
) {

}