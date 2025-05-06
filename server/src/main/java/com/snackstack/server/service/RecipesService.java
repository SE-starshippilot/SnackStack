package com.snackstack.server.service;

import com.snackstack.server.dao.RecipeIngredientDAO;
import com.snackstack.server.dao.RecipeStepsDAO;
import com.snackstack.server.dao.RecipesDAO;
import com.snackstack.server.model.Recipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class RecipesService {

  private static final Logger logger = LoggerFactory.getLogger(RecipesService.class);
  private final RecipesDAO recipesDAO;
  private final RecipeIngredientDAO recipeIngredientDAO;
  private final RecipeStepsDAO recipeStepsDAO;

  public RecipesService(RecipesDAO recipesDAO, RecipeIngredientDAO recipeIngredientDAO, RecipeStepsDAO recipeStepsDAO) {
    this.recipesDAO = recipesDAO;
    this.recipeIngredientDAO = recipeIngredientDAO;
    this.recipeStepsDAO = recipeStepsDAO;
  }

  // add a new recipe and return the recipe_id
  public Integer createRecipe(String recipeName, String description, Integer servings, String originId, String type) {
    logger.info("Creating new recipe: {}", recipeName);
    try {
      return recipesDAO.insert(recipeName, description, servings, originId, type);
    } catch (Exception e) {
      logger.error("Error creating recipe: {}", recipeName, e);
      throw e;
    }
  }

  // delete a recipe
  public void deleteRecipe(Integer recipeId) {
    logger.info("Deleting recipe with ID: {}", recipeId);
    try {
      recipesDAO.deleteById(recipeId);
    } catch (Exception e) {
      logger.error("Error deleting recipe with ID {}: {}", recipeId, e.getMessage());
      throw e;
    }
  }

  // get all recipes
  public List<Recipe> getAllRecipes() {
    logger.info("Fetching all recipes");
    return recipesDAO.findAll();
  }

  // get recipe by id
  public Recipe getRecipeById(Integer recipeId) {
    return getAllRecipes().stream()
        .filter(r -> r.recipeId().equals(recipeId))
        .findFirst()
        .orElse(null);
  }

  // Delete recipes in batches and delete related ingredients and steps
  public void deleteRecipesByIds(Integer[] recipeIds) {
    logger.info("Deleting recipes and related data for recipe_ids: {}", Arrays.toString(recipeIds));

    for (Integer recipeId : recipeIds) {
      // delete all ingredients for recipeIds
      logger.info("Deleting ingredients for recipe {}", recipeId);
      recipeIngredientDAO.deleteAllByRecipeId(recipeId);

      // delete all steps for recipeIds
      logger.info("Deleting steps for recipe {}", recipeId);
      recipeStepsDAO.deleteAllByRecipeId(recipeId);

      // delete recipe
      logger.info("Deleting recipe {}", recipeId);
      recipesDAO.deleteById(recipeId);
    }
  }
}
