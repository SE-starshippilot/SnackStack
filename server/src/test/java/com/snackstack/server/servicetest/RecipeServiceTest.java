package com.snackstack.server.servicetest;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import com.snackstack.server.dao.IngredientDAO;
import com.snackstack.server.dao.InventoryDAO;
import com.snackstack.server.dao.RecipeDAO;
import com.snackstack.server.dao.RecipeIngredientDAO;
import com.snackstack.server.dao.RecipeStepDAO;
import com.snackstack.server.dto.RecipeRequestDTO;
import com.snackstack.server.model.Recipe;
import com.snackstack.server.model.RecipeIngredient;
import com.snackstack.server.model.RecipeStep;
import com.snackstack.server.model.RecipeType;
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
  private RecipeRequestDTO recipeRequest = new RecipeRequestDTO(1, RecipeType.MAIN, null, null);

  @Before
  public void setUp() {
    Jdbi jdbi = jdbiRule.getJdbi();

    jdbi.useHandle(handle -> {
      handle.execute("""
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
          CREATE TABLE recipes (
            recipe_id SERIAL PRIMARY KEY,
            recipe_name TEXT NOT NULL,
            description TEXT,
            servings INT,
            recipe_origin_id VARCHAR(16),
            recipe_type VARCHAR(20) CHECK (recipe_type IS NULL OR recipe_type IN ('Main', 'Appetizer', 'Dessert', 'Breakfast', 'Snack')),
            is_favorite BOOLEAN DEFAULT FALSE,
            uuid VARCHAR(36) NOT NULL UNIQUE
            )
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
          )
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

  }

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
