package com.snackstack.server.servicetest;

import com.google.gson.Gson;
import com.snackstack.server.dao.*;
import com.snackstack.server.dto.*;
import com.snackstack.server.exceptions.RecordNotFound;
import com.snackstack.server.model.RecipeType;
import com.snackstack.server.service.InventoryService;
import com.snackstack.server.service.RecipeHistoryService;
import com.snackstack.server.service.RecipeService;
import com.snackstack.server.service.UserService;
import com.snackstack.server.service.llm.MockRecipeGenerator;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.testing.JdbiRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.snackstack.server.model.RecipeType.MAIN;
import static org.junit.Assert.*;

public class RecipeHistoryServiceTest {
  @Rule
  public JdbiRule jdbiRule = JdbiRule.h2().withPlugin(new SqlObjectPlugin());

  private final Gson gson = new Gson();
  private final MockRecipeGenerator recipeGenerator = new MockRecipeGenerator(gson);
  private RecipeHistoryService historyService;
  private RecipeHistoryDAO historyDAO;
  private RecipeDAO recipeDAO;
  private InventoryDAO inventoryDAO;
  private InventoryService inventoryService;
  private UserDAO userDAO;
  private UserService userService;
  private RecipeService recipeService;
  private RecipeStepDAO recipeStepDAO;
  private RecipeIngredientDAO recipeIngredientDAO;
  private IngredientDAO ingredientDAO;

  private int userId;
  private int recipeId = 1;

  private final String sampleUserName = "Nim Telson";
  private final String sampleUserEmail = "nim@snackstack.com";
  private final UserDTO sampleUser = new UserDTO(sampleUserName, sampleUserEmail);

  @Before
  public void setUp() {
    Jdbi jdbi = jdbiRule.getJdbi();
    // Create your tables
    jdbi.useHandle(handle -> {
      handle.execute("""
                CREATE TABLE users
          (
              user_id       SERIAL PRIMARY KEY,
              user_name     VARCHAR(16) NOT NULL,
              email         TEXT UNIQUE NOT NULL,
              created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
              last_login_at TIMESTAMP
          );
                CREATE TABLE ingredients (
                    ingredient_id SERIAL PRIMARY KEY,
                    ingredient_name varchar(255) NOT NULL UNIQUE
                );
                CREATE TYPE recipe_type AS ENUM ('MAIN', 'APPETIZER', 'DESSERT', 'BREAKFAST', 'SNACK');
                CREATE TABLE recipes (
                    recipe_id SERIAL PRIMARY KEY,
                    recipe_name TEXT NOT NULL,
                    description TEXT,
                    servings INT,
                    recipe_origin_id VARCHAR(16),
                    recipe_type TEXT,
                    is_favorite BOOLEAN DEFAULT false,
                    uuid VARCHAR(36) NOT NULL UNIQUE
                );
                CREATE TABLE recipe_history (
                    history_id SERIAL PRIMARY KEY,
                    user_id INT NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                    recipe_id INT NOT NULL REFERENCES recipes(recipe_id) ON DELETE CASCADE,
                    created_at TIMESTAMP NOT NULL DEFAULT now()
                );
                CREATE TABLE recipe_steps (
                    step_id SERIAL PRIMARY KEY,
                    recipe_id INT NOT NULL REFERENCES recipes(recipe_id) ON DELETE CASCADE,
                    step_number INT NOT NULL,
                    step_description TEXT NOT NULL,
                    UNIQUE (recipe_id, step_number)
                );
                CREATE TABLE recipe_ingredients (
                    recipe_id INT NOT NULL REFERENCES recipes(recipe_id) ON DELETE CASCADE,
                    ingredient_id INT NOT NULL REFERENCES ingredients(ingredient_id),
                    quantity DECIMAL(10, 2) NOT NULL,
                    unit VARCHAR(50),
                    note TEXT,
                    PRIMARY KEY (recipe_id, ingredient_id)
                );
                CREATE TABLE inventory_items
                (
                    inventory_item_id SERIAL PRIMARY KEY,
                    user_id           INT         NOT NULL
                        REFERENCES users (user_id)
                            ON DELETE CASCADE,
                    ingredient_id     INT         NOT NULL
                        REFERENCES ingredients (ingredient_id),
                    purchase_date     TIMESTAMP NOT NULL DEFAULT now()
                );
            """);
    });

    userDAO = jdbi.onDemand(UserDAO.class);
    recipeDAO = jdbi.onDemand(RecipeDAO.class);
    ingredientDAO = jdbi.onDemand(IngredientDAO.class);
    historyDAO = jdbi.onDemand(RecipeHistoryDAO.class);
    historyService = new RecipeHistoryService(historyDAO);
    userService = new UserService(userDAO);
    recipeStepDAO = jdbi.onDemand(RecipeStepDAO.class);
    recipeIngredientDAO = jdbi.onDemand(RecipeIngredientDAO.class);
    inventoryDAO = jdbi.onDemand(InventoryDAO.class);
    recipeService = new RecipeService(recipeGenerator, recipeDAO, recipeStepDAO,
        recipeIngredientDAO, ingredientDAO, inventoryDAO);
    inventoryService = new InventoryService(userDAO, inventoryDAO, ingredientDAO);

    // Prepare sample user & recipe
    userId = userService.createUser(sampleUser);
    List<IngredientDTO> recipeIngredients = new ArrayList<>();
    IngredientDTO ingredientDTO = new IngredientDTO("ingredient1", 1, "unit", "note");
    recipeIngredients.add(ingredientDTO);
    List<String> recipeSteps = new ArrayList<>();
    recipeSteps.add("step1");
    RecipeResponseDTO recipeResponseDTO = new RecipeResponseDTO("recipe_name", 1, "description", "origin_name", recipeIngredients, recipeSteps);
    List<String> availableIngredients = new ArrayList<>();
    availableIngredients.add("ingredient1");
    RecipeType recipeType = MAIN;
    List<String> mealOrigin = new ArrayList<>();
    mealOrigin.add("origin_name");
    List<String> allergies = new ArrayList<>();
    allergies.add("allergy");
    inventoryService.addInventoryRecord(sampleUserName, "ingredient1");
    RecipeGenerationDTO recipeGenerationDTO = new RecipeGenerationDTO(availableIngredients, 1, recipeType, mealOrigin, allergies);
    recipeService.saveRecipe(recipeResponseDTO, recipeGenerationDTO);

    // history will be added in each test
  }

  // add history
  @Test
  public void testAddToHistory_ShouldInsertSuccessfully() {
    int historyId = historyService.addToHistory(userId, recipeId);
    assertTrue(historyId > 0);

    List<RecipeHistoryDTO> history = historyService.getUserRecipeHistory(userId, 0, 10, true, false, null);
    assertEquals(1, history.size());
    assertEquals(recipeId, history.get(0).recipeId());
  }

  // test exist recipe
  @Test
  public void testGetHistoryEntry_ShouldReturnEnrichedData() {
    int historyId = historyService.addToHistory(userId, recipeId);

    RecipeHistoryDTO dto = historyService.getHistoryEntry(historyId, userId);
    assertNotNull(dto);
    assertEquals("recipe_name", dto.recipeName());
    assertEquals(1, dto.recipeStep().size());
    assertEquals("step1", dto.recipeStep().get(0));
    assertEquals(1, dto.recipeIngredients().size());
    assertEquals("ingredient1", dto.recipeIngredients().get(0).ingredientName());
  }

  // history with invalid userid
  @Test(expected = RecordNotFound.class)
  public void testGetHistoryEntry_InvalidUser_ShouldThrow() {
    int historyId = historyService.addToHistory(userId, recipeId);
    historyService.getHistoryEntry(historyId, userId + 1);
  }

  // test delete function
  @Test
  public void testDeleteHistoryEntry_ShouldRemoveEntry() {
    int historyId = historyService.addToHistory(userId, recipeId);
    int result = historyService.deleteHistoryEntry(historyId, userId);
    assertEquals(1, result);

    List<RecipeHistoryDTO> history = historyService.getUserRecipeHistory(userId, 0, 10, true, false, null);
    assertTrue(history.isEmpty());
  }

  // test searching function
  @Test
  public void testSearchHistory() {
    historyService.addToHistory(userId, recipeId);

    // Should find with keyword
    List<RecipeHistoryDTO> filtered = historyService.getUserRecipeHistory(userId, 0, 10, true, false, "recipe_name");
    assertEquals(1, filtered.size());

    // Should return empty for wrong keyword
    List<RecipeHistoryDTO> notFound = historyService.getUserRecipeHistory(userId, 0, 10, true, false, "invalid");
    assertTrue(notFound.isEmpty());
  }
}
