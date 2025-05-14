package com.snackstack.server.service;


import com.snackstack.server.dao.IngredientDAO;
import com.snackstack.server.dao.InventoryDAO;
import com.snackstack.server.dao.RecipeIngredientDAO;
import com.snackstack.server.dao.RecipeStepDAO;
import com.snackstack.server.dto.IngredientDTO;
import com.snackstack.server.dto.RecipeGenerationDTO;
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
  private final InventoryDAO inventoryDAO;

  public RecipeService(RecipeGenerator recipeGenerator, RecipeDAO recipeDAO,
      RecipeStepDAO recipeStepDAO, RecipeIngredientDAO recipeIngredientDAO,
      IngredientDAO ingredientDAO, InventoryDAO inventoryDAO) {
    this.recipeGenerator = recipeGenerator;
    this.recipeDAO = recipeDAO;
    this.recipeStepDAO = recipeStepDAO;
    this.recipeIngredientDAO = recipeIngredientDAO;
    this.ingredientDAO = ingredientDAO;
    this.inventoryDAO = inventoryDAO;
  }

  /**
   * Generate recipes given the user id and the front end request
   *
   * @param userId The user's id we are generating recipe for
   * @param req    The recipe generation request
   * @return List of generated recipes, all verified to use valid ingredients
   * @throws InvalidIngredientException If any recipe contains invalid ingredients
   */
  public List<RecipeResponseDTO> generateRecipeForUser(Integer userId,
      RecipeRequestDTO req) {

    // 1. Fetch the ingredients the user have
    List<String> userIngredients = getUserIngredients(userId);
    if (userIngredients.isEmpty()) {
      throw new IllegalArgumentException("The user inventory is empty.");
    }

    // 2.  compose the *internal* DTO handed to the generator
    RecipeGenerationDTO genDTO = new RecipeGenerationDTO(
        userIngredients,
        req.servings(),
        req.recipeType(),
        req.recipeOrigin(),
        req.allergies()
    );
    return generateAndSaveRecipe(genDTO);
  }

  public List<RecipeResponseDTO> generateAndSaveRecipe(RecipeGenerationDTO recipeGenerationDTO) {
    // 1. Generate recipes using LLM
    List<RecipeResponseDTO> generatedRecipes = this.recipeGenerator.generateRecipe(
        recipeGenerationDTO);

    // 2. Validate all recipes have valid ingredients before saving any
    validateAllRecipeIngredients(generatedRecipes);

    // 3. Save all recipes in a transaction
    for (RecipeResponseDTO recipe : generatedRecipes) {
      saveRecipe(recipe, recipeGenerationDTO);
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
      for (IngredientDTO ingredientDTO : recipe.recipeIngredients()) {
        String ingredientName = ingredientDTO.ingredientName();
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
   * @param recipeResponse The recipe to save
   * @param recipeRequest  The original request
   * @return The ID of the saved recipe
   */
  public void saveRecipe(RecipeResponseDTO recipeResponse, RecipeGenerationDTO recipeRequest) {
    // 1. Save recipe
    int recipeId = recipeDAO.addRecipe(
        recipeResponse.recipeName(),
        recipeResponse.description(),
        recipeRequest.servings(),
        recipeResponse.originName(),
        recipeRequest.recipeType(),
        recipeResponse.uuid()
    );

    // 2. Save recipe steps
    List<Integer> stepNumbers = IntStream.rangeClosed(1, recipeResponse.recipeSteps().size())
        .boxed()
        .toList();
    recipeStepDAO.addStepsForRecipe(recipeId, stepNumbers, recipeResponse.recipeSteps());

    // 3. Save recipe ingredients
    for (IngredientDTO ingredient : recipeResponse.recipeIngredients()) {
      // Get ingredient ID - we know it exists because we validated earlier
      Integer ingredientId = ingredientDAO.getIngredientIdByName(ingredient.ingredientName());

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

  /**
   * Fetches ingredient names from a user's inventory
   *
   * @param userId The user ID whose inventory ingredients will be fetched
   * @return List of ingredient names from the user's inventory
   */
  public List<String> getUserIngredients(long userId) {
    // Convert long to Integer since DAO methods use Integer
    Integer userIdInt = (int) userId;

    // Use the existing method in InventoryDAO that already does the join with ingredients table
    return inventoryDAO.getUserIngredientNames(userIdInt);
  }
}
