package com.snackstack.server.service;

import com.snackstack.server.dao.IngredientDAO;
import com.snackstack.server.dao.InventoryDAO;
import com.snackstack.server.dao.UserDAO;
import com.snackstack.server.exceptions.RecordNotFound;
import com.snackstack.server.model.Ingredient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class IngredientService {
  private final IngredientDAO ingredientDAO;

  private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

  public IngredientService(IngredientDAO ingredientDAO) {
    this.ingredientDAO = ingredientDAO;
  }

  // Create ingredient
  public long createIngredient(Integer ingredientId, String ingredientName) {
    // add a new record to inventory database
    logger.info("Creating ingredient {} in ingredients table", ingredientName);
    try {
      ingredientDAO.insert(ingredientId, ingredientName);
    } catch (Exception e) {
      logger.error("Error inserting ingredient: {}", ingredientName, e);
      throw e;
    }
    return ingredientId;
  }

  // Read (by ID)
  public Ingredient getIngredientById(Integer ingredientId) {
    logger.info("Fetching ingredient by ID: {}", ingredientId);
    return ingredientDAO.findById(ingredientId);
  }

  // Read (all)
  public List<Ingredient> getAllIngredients() {
    logger.info("Fetching all ingredients");
    return ingredientDAO.findAll();
  }

  // Update ingredient
  public void updateIngredient(Integer ingredientId, String newName) {
    logger.info("Updating ingredient {} to new name {}", ingredientId, newName);
    try {
      int updatedRows = ingredientDAO.update(ingredientId, newName);
      if (updatedRows == 0) {
        logger.warn("No ingredient found with ID {} to update", ingredientId);
      }
    } catch (Exception e) {
      logger.error("Error updating ingredient: {}", ingredientId, e);
      throw e;
    }
  }

  // Delete (by ID)
  public void deleteIngredientById(Integer ingredientId) {
    logger.info("Deleting ingredient by ID: {}", ingredientId);
    try {
      ingredientDAO.deleteById(ingredientId);
    } catch (Exception e) {
      logger.error("Error deleting ingredient by ID: {}", ingredientId, e);
      throw e;
    }
  }
}
