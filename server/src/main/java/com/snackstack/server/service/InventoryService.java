package com.snackstack.server.service;

import com.snackstack.server.dao.IngredientDAO;
import com.snackstack.server.dao.InventoryDAO;
import com.snackstack.server.dao.UserDAO;
import com.snackstack.server.exceptions.RecordNotFound;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryService {

  private final UserDAO userDAO;
  private final InventoryDAO inventoryDAO;

  private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

  public InventoryService(UserDAO userDAO, InventoryDAO inventoryDAO) {
    this.userDAO = userDAO;
    this.inventoryDAO = inventoryDAO;
  }

  private Integer getUserId(String userName) {
    try {
      logger.info("Searching user with username: {}", userName);
      Optional<Integer> uid = userDAO.getUserIdByName(userName);
      if (uid.isEmpty()) {
        throw new RecordNotFound("User not found");
      }
      return uid.get();
    } catch (Exception e) {
      logger.error("Error searching user: {}", userName, e);
      throw e;
    }
  }

  public long createIngredient(String userName, String ingredientName) {
    // add a new record to inventory database
    logger.info("Creating ingredient {} for user with name {}", ingredientName, userName);
    try {
      Integer userId = getUserId(userName);
      Instant now = Instant.now();
      inventoryDAO.insert(userId, ingredientName, now);
    } catch (Exception e) {
      logger.error("Error searching user: {}", userName, e);
      throw e;
    }
    return 0;
  }

  public List<String> getIngredients(String userName) {
    // fetch all the ingredients of current user
    logger.info("Getting all ingredients for user with name: {}", userName);
    try {
      Integer userId = getUserId(userName);
      return inventoryDAO.getAllIngredientsOfUser(userId);
    } catch (Exception e) {
      logger.error("Error searching user: {}", userName, e);
      throw e;
    }
  }

  public Integer deleteIngredient(String userName, String ingredientName) {
    logger.info("Deleting ingredient {} for user with name {}", ingredientName, userName);
    try {
      Integer userId = getUserId(userName);
      int deleted = inventoryDAO.deleteIngredient(userId, ingredientName);
      if (deleted == 0) {                                   // nothing matched → 404 later
        throw new RecordNotFound("Ingredient not found");
      }
      return deleted;
    } catch (Exception e) {
      logger.error("Error deleting ingredient {} for user: {}", ingredientName, userName, e);
    }
    return 0;
  }
}
