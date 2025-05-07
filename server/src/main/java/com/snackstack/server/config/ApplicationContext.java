package com.snackstack.server.config;

import com.google.gson.Gson;
import com.snackstack.server.controller.*;
import com.snackstack.server.dao.*;
import com.snackstack.server.service.*;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

public class ApplicationContext implements AutoCloseable {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationContext.class);

  private final DBConfig dbConfig;
  private final Gson gson;
  private final UserDAO userDAO;
  private final InventoryDAO inventoryDAO;
  private final IngredientDAO ingredientDAO;
  private final RecipesDAO recipesDAO;
  private final RecipeIngredientDAO recipeIngredientDAO;
  private final RecipeStepsDAO recipeStepsDAO;
  private final UserService userService;
  private final InventoryService inventoryService;
  private final RecipesService recipesService;
  private final RecipeIngredientService recipeIngredientService;
  private final RecipeStepsService recipeStepsService;
  private final List<Controller> controllers = new ArrayList<>();

  public ApplicationContext() {
    logger.info("Initializing ApplicationContext");

    // Initialize configuration
    AppConfig config = AppConfig.getInstance();
    this.dbConfig = config.configDB();
    Jdbi jdbi = dbConfig.getJdbi();

    // Initialize common components
    this.gson = new Gson();

    // Initialize DAOs
    this.userDAO = jdbi.onDemand(UserDAO.class);
    this.inventoryDAO = jdbi.onDemand(InventoryDAO.class);
    this.ingredientDAO = jdbi.onDemand(IngredientDAO.class);
    this.recipesDAO = jdbi.onDemand(RecipesDAO.class);
    this.recipeIngredientDAO = jdbi.onDemand(RecipeIngredientDAO.class);
    this.recipeStepsDAO = jdbi.onDemand(RecipeStepsDAO.class);

    // Initialize services
    this.userService = new UserService(userDAO);
    this.inventoryService = new InventoryService(userDAO, inventoryDAO, ingredientDAO);
    this.recipesService = new RecipesService(recipesDAO, recipeIngredientDAO, recipeStepsDAO);
    this.recipeIngredientService = new RecipeIngredientService(recipeIngredientDAO);
    this.recipeStepsService = new RecipeStepsService(recipeStepsDAO);

    // Initialize controllers
    controllers.add(new UserController(userDAO, userService, gson));
    controllers.add(new InventoryController(inventoryService, gson));
    controllers.add(new RecipesController(recipesService, gson));
    controllers.add(new RecipeIngredientController(recipeIngredientService, gson));
    controllers.add(new RecipeStepsController(recipeStepsService, gson));

    logger.info("ApplicationContext initialization completed");
  }

  public void registerAllRoutes() {
    logger.info("Registering all controller routes");
    for (Controller controller : controllers) {
      controller.registerRoutes();
    }
    logger.info("All routes registered successfully");
  }

  // Getters for all resources
  public Gson getGson() {
    return gson;
  }

  public UserDAO getUserDAO() {
    return userDAO;
  }

  public InventoryDAO getInventoryDAO() {
    return inventoryDAO;
  }

  public IngredientDAO getIngredientDAO() {
    return ingredientDAO;
  }

  public RecipesDAO getRecipesDAO() {
    return recipesDAO;
  }

  public RecipeIngredientDAO getRecipeIngredientDAO() {
    return recipeIngredientDAO;
  }

  public RecipeStepsDAO getRecipeStepsDAO() {
    return recipeStepsDAO;
  }

  public UserService getUserService() {
    return userService;
  }

  public InventoryService getInventoryService() {return inventoryService;}

  public RecipesService getRecipesService() {
    return recipesService;
  }
  public RecipeIngredientService getIRecipeIngredientService() {
    return recipeIngredientService;
  }
  public RecipeStepsService getRecipeStepsService() {
    return recipeStepsService;
  }


  @Override
  public void close() {
    logger.info("Shutting down ApplicationContext");
    if (dbConfig != null) {
      logger.info("Closing database connections");
      dbConfig.close();
    }
    logger.info("ApplicationContext shutdown completed");
  }
}