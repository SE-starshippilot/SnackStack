package com.snackstack.server.service;


import com.snackstack.server.dao.IngredientDAO;
import com.snackstack.server.dao.RecipeIngredientDAO;
import com.snackstack.server.dao.RecipeStepDAO;
import com.snackstack.server.dto.IngredientDTO;
import com.snackstack.server.dto.RecipeRequestDTO;
import com.snackstack.server.dto.RecipeResponseDTO;
import com.snackstack.server.dao.RecipeDAO;
import com.snackstack.server.exceptions.InvalidIngredientException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RecipeService {

  private final RecipeGenerator recipeGenerator;
  private final RecipeDAO recipeDAO;
  private final RecipeStepDAO recipeStepDAO;
  private final RecipeIngredientDAO recipeIngredientDAO;
  private final IngredientDAO ingredientDAO;

  public RecipeService(RecipeGenerator recipeGenerator, RecipeDAO recipeDAO,
      RecipeStepDAO recipeStepDAO, RecipeIngredientDAO recipeIngredientDAO,
      IngredientDAO ingredientDAO) {
    this.recipeGenerator = recipeGenerator;
    this.recipeDAO = recipeDAO;
    this.recipeStepDAO = recipeStepDAO;
    this.recipeIngredientDAO = recipeIngredientDAO;
    this.ingredientDAO = ingredientDAO;
  }

  /**
   * Generates recipes and saves them to the database. The entire operation is atomic - if any
   * ingredient in any recipe doesn't exist, no recipes will be saved.
   *
   * @param recipeRequestDTO The recipe generation request
   * @return List of generated recipes, all verified to use valid ingredients
   * @throws InvalidIngredientException If any recipe contains invalid ingredients
   */
  public List<RecipeResponseDTO> generateRecipes(RecipeRequestDTO recipeRequestDTO) {
    // 1. Generate recipes using LLM
    List<RecipeResponseDTO> generatedRecipes = this.recipeGenerator.generateRecipe(
        recipeRequestDTO);

    // 2. Validate all recipes have valid ingredients before saving any
    validateAllRecipeIngredients(generatedRecipes);

    // 3. Save all recipes in a transaction
    for (RecipeResponseDTO recipe : generatedRecipes) {
      saveRecipe(recipe, recipeRequestDTO);
    }
    return generatedRecipes;
  }

  /**
   * Checks if all ingredients in all recipes exist in the database
   *
   * @param recipes List of recipes to validate
   * @throws InvalidIngredientException If any recipe contains invalid ingredients
   */
  private void validateAllRecipeIngredients(List<RecipeResponseDTO> recipes) {
    List<String> invalidIngredients = new ArrayList<>();

    for (RecipeResponseDTO recipe : recipes) {
      for (IngredientDTO ingredientDTO : recipe.recipe_ingredients()) {
        String ingredientName = ingredientDTO.ingredient_name();
        if (!ingredientDAO.ingredientExists(ingredientName)) {
          invalidIngredients.add(ingredientName);
        }
      }
    }

    if (!invalidIngredients.isEmpty()) {
      throw new InvalidIngredientException("Recipe contains invalid ingredients: " +
          String.join(", ", invalidIngredients));
    }
  }


  /**
   * Saves a single recipe with its steps and ingredients
   *
   * @param recipeResponse   The recipe to save
   * @param recipeRequest The original request
   * @return The ID of the saved recipe
   */
  private void saveRecipe(RecipeResponseDTO recipeResponse, RecipeRequestDTO recipeRequest) {
    // 1. Save recipe
    int recipeId = recipeDAO.addRecipe(
        recipeResponse.recipe_name(),
        recipeResponse.description(),
        recipeRequest.servings(),
        recipeResponse.origin_name(),
        recipeRequest.meal_type(),
        recipeResponse.uuid()
    );

    // 2. Save recipe steps
    List<Integer> stepNumbers = IntStream.rangeClosed(1, recipeResponse.recipe_steps().size())
        .boxed()
        .toList();
    recipeStepDAO.addStepsForRecipe(recipeId, stepNumbers, recipeResponse.recipe_steps());

    // 3. Save recipe ingredients
    for (IngredientDTO ingredient : recipeResponse.recipe_ingredients()) {
      // Get ingredient ID - we know it exists because we validated earlier
      Integer ingredientId = ingredientDAO.getIngredientIdByName(ingredient.ingredient_name());

      // Add to recipe_ingredients table
      recipeIngredientDAO.addIngredientToRecipe(
          recipeId,
          ingredientId,
          ingredient.quantity(),
          ingredient.unit(),
          ingredient.note()
      );
    }

  }

  /**
   * Deletes a recipe by UUID
   *
   * @param uuid Recipe UUID
   * @return True if successful
   */
  public boolean deleteRecipe(String uuid) {
    return recipeDAO.deleteRecipeByUuid(uuid) > 0;
  }

}
