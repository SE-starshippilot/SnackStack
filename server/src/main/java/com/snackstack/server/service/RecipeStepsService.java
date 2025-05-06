package com.snackstack.server.service;

import com.snackstack.server.dao.RecipeStepsDAO;
import com.snackstack.server.model.RecipeSteps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RecipeStepsService {

  private static final Logger logger = LoggerFactory.getLogger(RecipeStepsService.class);
  private final RecipeStepsDAO recipeStepsDAO;

  public RecipeStepsService(RecipeStepsDAO recipeStepsDAO) {
    this.recipeStepsDAO = recipeStepsDAO;
  }

  // add one step for a recipe
  public void addStep(Integer recipeId, Integer stepNumber, String stepDescription) {
    logger.info("Adding step {} to recipe {}: {}", stepNumber, recipeId, stepDescription);
    try {
      recipeStepsDAO.insert(recipeId, stepNumber, stepDescription);
    } catch (Exception e) {
      logger.error("Error adding step to recipe {}: {}", recipeId, e.getMessage());
      throw e;
    }
  }

  // delete one step from a specific recipe
  public void deleteStep(Integer recipeId, Integer stepNumber) {
    logger.info("Deleting step {} from recipe {}", stepNumber, recipeId);
    recipeStepsDAO.delete(recipeId, stepNumber);
  }

  // delete all steps for a recipe
  public void deleteAllSteps(Integer recipeId) {
    logger.info("Deleting all steps for recipe {}", recipeId);
    recipeStepsDAO.deleteAllByRecipeId(recipeId);
  }

  // get all steps for a recipe (in order)
  public List<RecipeSteps> getStepsByRecipeId(Integer recipeId) {
    logger.info("Fetching steps for recipe {}", recipeId);
    return recipeStepsDAO.findByRecipeId(recipeId);
  }
}
