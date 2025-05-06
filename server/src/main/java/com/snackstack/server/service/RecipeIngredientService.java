package com.snackstack.server.service;

import com.snackstack.server.dao.RecipeIngredientDAO;
import com.snackstack.server.model.RecipeIngredient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RecipeIngredientService {

  private static final Logger logger = LoggerFactory.getLogger(RecipeIngredientService.class);
  private final RecipeIngredientDAO dao;

  public RecipeIngredientService(RecipeIngredientDAO dao) {
    this.dao = dao;
  }

  // add an ingredient to a specific recipe
  public void addIngredientToRecipe(Integer recipeId, Integer ingredientId, Integer quantity, String unit, String note) {
    logger.info("Adding ingredient {} to recipe {}", ingredientId, recipeId);
    try {
      dao.insert(recipeId, ingredientId, quantity, unit, note);
    } catch (Exception e) {
      logger.error("Error adding ingredient to recipe: {}", e.getMessage());
      throw e;
    }
  }

  // delete an ingredient in a specific recipe
  public void removeIngredientFromRecipe(Integer recipeId, Integer ingredientId) {
    logger.info("Removing ingredient {} from recipe {}", ingredientId, recipeId);
    dao.delete(recipeId, ingredientId);
  }

  // delete all ingredients in an recipe
  public void removeAllIngredientsFromRecipe(Integer recipeId) {
    logger.info("Removing all ingredients from recipe {}", recipeId);
    dao.deleteAllByRecipeId(recipeId);
  }

  // get all ingredients for a specific recipe
  public List<RecipeIngredient> getIngredientsByRecipe(Integer recipeId) {
    logger.info("Fetching ingredients for recipe {}", recipeId);
    return dao.findByRecipeId(recipeId);
  }
}
