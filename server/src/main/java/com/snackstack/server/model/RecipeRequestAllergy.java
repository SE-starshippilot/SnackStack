package com.snackstack.server.model;

public record RecipeRequestAllergy(
    Integer requestAllergyId,
    Integer requestId,
    String allergyName
) {

}
