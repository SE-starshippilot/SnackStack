package com.snackstack.server.service;

import java.util.List;

import com.snackstack.server.dto.RecipeRequestDTO;
import com.snackstack.server.dto.RecipeResponseDTO;
import com.snackstack.server.exceptions.LLMServiceException;

public interface RecipeGenerator {

  List<RecipeResponseDTO> generateRecipe(RecipeRequestDTO recipeRequest) throws LLMServiceException;

  default String getProviderName() {
    return "Unknown";
  }
}
