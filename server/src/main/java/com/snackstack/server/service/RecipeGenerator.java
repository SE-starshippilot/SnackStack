package com.snackstack.server.service;

import com.snackstack.server.dto.RecipeRequestDTO;
import com.snackstack.server.dto.RecipeResponseDTO;
import com.snackstack.server.exceptions.LLMServiceException;
import java.util.List;

public interface RecipeGenerator {

  List<RecipeResponseDTO> generateRecipe(RecipeRequestDTO recipeRequest) throws LLMServiceException;

  default String getProviderName() {
    return "Unknown";
  }
}