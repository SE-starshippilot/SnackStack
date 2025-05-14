package com.snackstack.server.servicetest;

import static com.snackstack.server.model.RecipeType.MAIN;
import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.snackstack.server.dao.*;
import com.snackstack.server.dto.*;
import com.snackstack.server.exceptions.InvalidIngredientException;
import com.snackstack.server.model.Recipe;
import com.snackstack.server.model.RecipeIngredient;
import com.snackstack.server.model.RecipeStep;
import com.snackstack.server.model.RecipeType;
import com.snackstack.server.service.InventoryService;
import com.snackstack.server.service.RecipeHistoryService;
import com.snackstack.server.service.RecipeService;
import com.snackstack.server.service.UserService;
import com.snackstack.server.service.llm.MockRecipeGenerator;

import java.util.ArrayList;
import java.util.List;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.jdbi.v3.testing.JdbiRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class RecipeServiceTest {

  @Rule
  public JdbiRule jdbiRule = JdbiRule.h2().withPlugin(new SqlObjectPlugin());

  private final Gson gson = new Gson();
  private final MockRecipeGenerator recipeGenerator = new MockRecipeGenerator(gson);
  private RecipeDAO recipeDAO;
  private RecipeStepDAO recipeStepDAO;
  private RecipeIngredientDAO recipeIngredientDAO;
  private IngredientDAO ingredientDAO;
  private RecipeService recipeService;
  private InventoryDAO inventoryDAO;
  private InventoryService inventoryService;
  private UserDAO userDAO;
  private UserService userService;
  private final String sampleUserName = "Nim Telson";
  private final String sampleUserEmail = "nim@snackstack.com";
  private final UserDTO sampleUser = new UserDTO(sampleUserName, sampleUserEmail);
  private int userId;
  private int recipeId = 1;
  private RecipeRequestDTO recipeRequest = new RecipeRequestDTO(1, RecipeType.MAIN, null, null);

  @Before
  public void setUp() {
    Jdbi jdbi = jdbiRule.getJdbi();

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
              ingredient_name VARCHAR(255) NOT NULL UNIQUE
          )
          """);


      handle.execute("""
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
          """);

      handle.execute("""
          CREATE TABLE recipe_steps (
              step_id SERIAL PRIMARY KEY,
              recipe_id INT NOT NULL,
              step_number INT NOT NULL,
              step_description TEXT NOT NULL,
              UNIQUE (recipe_id, step_number),
              FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id) ON DELETE CASCADE
          )
          """);

      handle.execute("""
          CREATE TABLE recipe_ingredients (
              recipe_id INT NOT NULL,
              ingredient_id INT NOT NULL,
              quantity DECIMAL(10, 2) NOT NULL,
              unit VARCHAR(50),
              note TEXT,
              PRIMARY KEY (recipe_id, ingredient_id),
              FOREIGN KEY (recipe_id) REFERENCES recipes(recipe_id) ON DELETE CASCADE,
              FOREIGN KEY (ingredient_id) REFERENCES ingredients(ingredient_id) ON DELETE RESTRICT
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
    // Initialize DAOs
    recipeDAO = jdbi.onDemand(RecipeDAO.class);
    recipeStepDAO = jdbi.onDemand(RecipeStepDAO.class);
    recipeIngredientDAO = jdbi.onDemand(RecipeIngredientDAO.class);
    ingredientDAO = jdbi.onDemand(IngredientDAO.class);
    inventoryDAO = jdbi.onDemand(InventoryDAO.class);

    userDAO = jdbi.onDemand(UserDAO.class);
    recipeDAO = jdbi.onDemand(RecipeDAO.class);
    ingredientDAO = jdbi.onDemand(IngredientDAO.class);
    userService = new UserService(userDAO);
    recipeStepDAO = jdbi.onDemand(RecipeStepDAO.class);
    recipeIngredientDAO = jdbi.onDemand(RecipeIngredientDAO.class);
    inventoryDAO = jdbi.onDemand(InventoryDAO.class);
    inventoryService = new InventoryService(userDAO, inventoryDAO, ingredientDAO);

    // Initialize the RecipeService with the DAOs and mock recipe generator
    recipeService = new RecipeService(recipeGenerator, recipeDAO, recipeStepDAO,
        recipeIngredientDAO, ingredientDAO, inventoryDAO);

    // populate ingredient database
    List<String> sampleIngredients = List.of(
        "Chicken breast",
        "Rice",
        "Carrots",
        "Onions",
        "Olive oil",
        "Spinach",
        "Garlic powder",
        "Canned tomatoes",
        "ingredient1"
    );
    ingredientDAO.batchAddIngredients(sampleIngredients);

    // Prepare sample user & recipe
    userId = userService.createUser(sampleUser);
    // create recipes
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
//    inventoryService.addInventoryRecord(sampleUserName, "ingredient1");
    RecipeGenerationDTO recipeGenerationDTO = new RecipeGenerationDTO(availableIngredients, 1, recipeType, mealOrigin, allergies);
    recipeService.saveRecipe(recipeResponseDTO, recipeGenerationDTO);

  }

  // save another recipe into database
  @Test
  public void generateRecipesUsingMock_ShouldStoreToDatabase() {
    // create recipes
    List<IngredientDTO> recipeIngredients = new ArrayList<>();
    IngredientDTO ingredientDTO = new IngredientDTO("ingredient2", 1, "unit", "note");
    recipeIngredients.add(ingredientDTO);
    List<String> recipeSteps = new ArrayList<>();
    recipeSteps.add("step1");
    RecipeResponseDTO recipeResponseDTO = new RecipeResponseDTO("recipe_name2", 1, "description", "origin_name", recipeIngredients, recipeSteps);
    List<String> availableIngredients = new ArrayList<>();
    availableIngredients.add("ingredient2");
    RecipeType recipeType = MAIN;
    List<String> mealOrigin = new ArrayList<>();
    mealOrigin.add("origin_name");
    List<String> allergies = new ArrayList<>();
    allergies.add("allergy");
    inventoryService.addInventoryRecord(sampleUserName, "ingredient2");
    RecipeGenerationDTO recipeGenerationDTO = new RecipeGenerationDTO(availableIngredients, 1, recipeType, mealOrigin, allergies);
    recipeService.saveRecipe(recipeResponseDTO, recipeGenerationDTO);

    // check recipe table
    List<Recipe> recipes = recipeDAO.getAllRecipes();
    assertEquals(2, recipes.size());

    // check recipe step table
    int recipe = 2;
    List<RecipeStep> recipeStep = recipeStepDAO.getStepsForRecipe(recipe);
    assertEquals(1, recipeStep.size());
    // check recipe ingredient table
    List<RecipeIngredient> ingredients = recipeIngredientDAO.getIngredientsForRecipe(recipe);
    assertEquals(1, ingredients.size());
  }

  // should throw an exception if the user didn't create any inventory and wants to generate a recipe
  @Test(expected = IllegalArgumentException.class)
  public void generateRecipeForUser_withEmptyInventory_shouldThrowException() {
    // user didn't create any inventory
    int emptyUserId = userService.createUser(new UserDTO("Test User", "empty@user.com"));

    RecipeRequestDTO requestDTO = new RecipeRequestDTO(
        2,
        RecipeType.MAIN,
        List.of("China"),
        List.of("peanuts")
    );

    // should throw an exception: The user inventory is empty.
    recipeService.generateRecipeForUser(emptyUserId, requestDTO);
  }

  // delete a recipe
  @Test
  public void deleteRecipe_shouldRemoveFromDatabase() {
    // get all recipes
    List<Recipe> recipes = recipeDAO.getAllRecipes();
    assertEquals(1, recipes.size());

    String uuid = recipes.get(0).uuid();
    boolean deleted = recipeService.deleteRecipe(uuid);

    assertEquals(true, deleted);
    assertEquals(0, recipeDAO.getAllRecipes().size());
  }
}
