package com.snackstack.server.model;

public record RecipeRequestOrigin(
    Integer requestOriginsId,
    Integer requestId,
    String originName
) {

}
