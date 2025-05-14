package com.snackstack.server.servicetest;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.snackstack.server.dao.*;
import com.snackstack.server.dto.*;
import com.snackstack.server.model.Recipe;
import com.snackstack.server.model.RecipeIngredient;
import com.snackstack.server.model.RecipeStep;
import com.snackstack.server.model.RecipeType;
import com.snackstack.server.service.InventoryService;
import com.snackstack.server.service.RecipeService;
import com.snackstack.server.service.llm.MockRecipeGenerator;
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
          CREATE DOMAIN IF NOT EXISTS recipe_type AS VARCHAR(20) 
          CHECK (VALUE IN ('Main', 'Appetizer', 'Dessert', 'Breakfast', 'Snack'))
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

    // Initialize the RecipeService with the DAOs and mock recipe generator
    recipeService = new RecipeService(recipeGenerator, recipeDAO, recipeStepDAO,
        recipeIngredientDAO, ingredientDAO,inventoryDAO);

    // populate ingredient database
    List<String> sampleIngredients = List.of(
        "Chicken breast",
        "Rice",
        "Carrots",
        "Onions",
        "Olive oil",
        "Spinach",
        "Garlic powder",
        "Canned tomatoes"
    );
    ingredientDAO.batchAddIngredients(sampleIngredients);

    userService.createUser(sampleUser);
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
//    recipeService.generateRecipes(recipeRequest);
//    // check recipe table
//    List<Recipe> recipes = recipeDAO.getAllRecipes();
//    assertEquals(3, recipes.size());
//
//    // check recipe step table
//    int friedRiceId = 3;
//    List<RecipeStep> friedRiceSteps = recipeStepDAO.getStepsForRecipe(friedRiceId);
//    assertEquals(4, friedRiceSteps.size());
//    // check recipe ingredient table
//    List<RecipeIngredient> friedRiceIngredients = recipeIngredientDAO.getIngredientsForRecipe(friedRiceId);
//    assertEquals(5, friedRiceIngredients.size());
  }
}
