package com.snackstack.server.model;

import java.time.Instant;

public record RecipeRequest(
  Integer recipeRequestId,
  Integer userId,
  Integer servings,
  String recipeType,
  Instant createdAt
) {

}

