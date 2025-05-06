package com.snackstack.server.config;

import com.google.gson.Gson;
import com.snackstack.server.controller.*;
import com.snackstack.server.dao.*;
import com.snackstack.server.service.*;
import com.snackstack.server.util.DBUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;
import io.github.cdimascio.dotenv.Dotenv;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Bootstrap {

  private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);

  public static void init() {
    logger.info("Initializing application...");
    try {
      Gson gson = new Gson();

      Jdbi jdbi = DBUtil.getJdbi();

      logger.info("Initializing DAO and service layers");
      UserDAO userDAO = jdbi.onDemand(UserDAO.class);
      InventoryDAO inventoryDAO = jdbi.onDemand(InventoryDAO.class);
      IngredientDAO ingredientDAO = jdbi.onDemand(IngredientDAO.class);
      RecipesDAO recipesDAO = jdbi.onDemand(RecipesDAO.class);
      RecipeIngredientDAO recipeIngredientDAO = jdbi.onDemand(RecipeIngredientDAO.class);
      RecipeStepsDAO recipeStepsDAO = jdbi.onDemand(RecipeStepsDAO.class);
      UserService userService = new UserService(userDAO);
      InventoryService inventoryService = new InventoryService(userDAO, inventoryDAO);
      IngredientService ingredientService = new IngredientService(ingredientDAO);
      RecipesService recipesService = new RecipesService(recipesDAO, recipeIngredientDAO, recipeStepsDAO);
      RecipeIngredientService recipeIngredientService = new RecipeIngredientService(recipeIngredientDAO);
      RecipeStepsService recipeStepsService = new RecipeStepsService(recipeStepsDAO);


      logger.info("Registering controller routes");
      UserController.registerRoutes(userDAO, userService, gson);
      InventoryController.registerRoutes(inventoryService, gson);
      IngredientController.registerRoutes(ingredientService, gson);

      RecipesController.registerRoutes(recipesService, gson);
      RecipeIngredientController.registerRoutes(recipeIngredientService, gson);
      RecipeStepsController.registerRoutes(recipeStepsService, gson);

      logger.info("Application initialization completed successfully");
    } catch (Exception e) {
      logger.error("Failed to initialize application", e);
      throw e;
    }
  }

}
