package com.snackstack.server.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snackstack.server.controller.*;
import com.snackstack.server.dao.*;
import com.snackstack.server.service.*;
import com.snackstack.server.service.llm.*;
import com.snackstack.server.utils.InstantTypeAdapter;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ApplicationContext implements AutoCloseable {

  private static final Logger logger = LoggerFactory.getLogger(ApplicationContext.class);

  private final DBConfig dbConfig;
  private OllamaConfig ollamaConfig;
  private final Gson gson;
  // DAOs
  private final UserDAO userDAO;
  private final InventoryDAO inventoryDAO;
  private final IngredientDAO ingredientDAO;
  private final RecipeDAO recipeDAO;
  private final RecipeIngredientDAO recipeIngredientDAO;
  private final RecipeHistoryDAO recipeHistoryDAO;
  private final RecipeStepDAO recipeStepDAO;

  private final RecipeGenerator recipeGenerator;
  // Services
  private final UserService userService;
  private final InventoryService inventoryService;
  private final RecipeHistoryService recipeHistoryService;
  private final RecipeService recipeService;
  private final List<Controller> controllers = new ArrayList<>();

  public ApplicationContext(boolean mock) {
    logger.info("Initializing ApplicationContext");

    // Initialize configuration
    AppConfig config = AppConfig.getInstance();
    this.dbConfig = config.configDB();

    Jdbi jdbi = dbConfig.getJdbi();

    // Initialize common components
    this.gson = new GsonBuilder()
        .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
        .create();

    // Initialize Recipe Generator
    if (mock) {
      logger.warn("Using Mock LLM!!!");
      this.recipeGenerator = new MockRecipeGenerator(this.gson);
    } else {
      this.ollamaConfig = config.configOllama();
      this.recipeGenerator = new OllamaRecipeGenerator(this.ollamaConfig, this.gson);
    }

    // Initialize DAOs
    this.userDAO = jdbi.onDemand(UserDAO.class);
    this.inventoryDAO = jdbi.onDemand(InventoryDAO.class);
    this.ingredientDAO = jdbi.onDemand(IngredientDAO.class);
    this.recipeHistoryDAO = jdbi.onDemand(RecipeHistoryDAO.class);
    this.recipeDAO = jdbi.onDemand(RecipeDAO.class);
    this.recipeStepDAO = jdbi.onDemand(RecipeStepDAO.class);
    this.recipeIngredientDAO = jdbi.onDemand(RecipeIngredientDAO.class);

    // Initialize services
    this.userService = new UserService(userDAO);
    this.inventoryService = new InventoryService(userDAO, inventoryDAO, ingredientDAO);
    this.recipeHistoryService = new RecipeHistoryService(recipeHistoryDAO, recipeDAO);
    this.recipeService = new RecipeService(recipeGenerator, recipeDAO, recipeStepDAO, recipeIngredientDAO, ingredientDAO, inventoryDAO);

    // Initialize controllers
    controllers.add(new UserController(userService, gson));
    controllers.add(new InventoryController(inventoryService, gson));
    controllers.add(new RecipeHistoryController(recipeHistoryService, gson));
    controllers.add(new RecipeController(recipeService, gson));

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


  public RecipeIngredientDAO getRecipeIngredientDAO() {
    return recipeIngredientDAO;
  }


  public UserService getUserService() {
    return userService;
  }

  public InventoryService getInventoryService() {return inventoryService;}

  public RecipeService getRecipesService() {
    return recipeService;
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