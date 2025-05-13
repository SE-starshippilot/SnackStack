package com.snackstack.server.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snackstack.server.controller.Controller;
import com.snackstack.server.controller.InventoryController;
import com.snackstack.server.controller.RecipeController;
import com.snackstack.server.controller.RecipeHistoryController;
import com.snackstack.server.controller.UserController;
import com.snackstack.server.dao.IngredientDAO;
import com.snackstack.server.dao.InventoryDAO;
import com.snackstack.server.dao.RecipeDAO;
import com.snackstack.server.dao.RecipeHistoryDAO;
import com.snackstack.server.dao.RecipeIngredientDAO;
import com.snackstack.server.dao.RecipeStepDAO;
import com.snackstack.server.dao.UserDAO;
import com.snackstack.server.service.InventoryService;
import com.snackstack.server.service.RecipeGenerator;
import com.snackstack.server.service.RecipeHistoryService;
import com.snackstack.server.service.RecipeService;
import com.snackstack.server.service.UserService;
import com.snackstack.server.service.llm.MockRecipeGenerator;
import com.snackstack.server.service.llm.OllamaRecipeGenerator;
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
  private final UserDAO userDAO;
  private final InventoryDAO inventoryDAO;
  private final IngredientDAO ingredientDAO;
  private final RecipeGenerator recipeGenerator;
  private final UserService userService;
  private final InventoryService inventoryService;
  private final RecipeHistoryDAO recipeHistoryDAO;
  private final RecipeHistoryService recipeHistoryService;
  private final RecipeDAO recipeDAO;
  private final RecipeStepDAO recipeStepDAO;
  private final RecipeIngredientDAO recipeIngredientDAO;
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
    this.recipeHistoryService = new RecipeHistoryService(recipeHistoryDAO);
    this.recipeService = new RecipeService(recipeGenerator, recipeDAO, recipeStepDAO, recipeIngredientDAO, ingredientDAO);

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

  public UserService getUserService() {
    return userService;
  }

  public InventoryService getInventoryService() {
    return inventoryService;
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