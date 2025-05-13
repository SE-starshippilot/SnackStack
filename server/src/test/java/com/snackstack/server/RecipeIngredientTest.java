package com.snackstack.server;

import com.google.gson.Gson;
import com.snackstack.server.controller.RecipeIngredientController;
import com.snackstack.server.model.RecipeIngredient;
import com.snackstack.server.service.RecipeIngredientService;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;

import java.util.List;

public class RecipeIngredientTest {

  private static final int PORT = 4567;
  private static final String BASE_URL = "http://localhost:" + PORT + "/api/recipes";
  private static RecipeIngredientService mockService;
  private static Gson gson;

  @BeforeAll
  public static void setup() throws Exception {
    mockService = mock(RecipeIngredientService.class);
    gson = new Gson();
    RecipeIngredientController controller = new RecipeIngredientController(mockService, gson);
    controller.registerRoutes();

    // Wait for Spark to start
    Thread.sleep(500);
  }

  @AfterAll
  public static void tearDown() {
    spark.Spark.stop();
  }

  @Test
  public void testGetIngredients() {
    Integer recipeId = 1;
    List<RecipeIngredient> mockIngredients = List.of(
        new RecipeIngredient(1, 1, 2, "kg", "Fresh"),
        new RecipeIngredient(2, 2, 1, "kg", "Mozzarella")
    );

    when(mockService.getIngredientsByRecipe(recipeId)).thenReturn(mockIngredients);

    HttpResponse<JsonNode> response = Unirest.get(BASE_URL + "/1/ingredients").asJson();

    Assertions.assertEquals(200, response.getStatus());
    Assertions.assertTrue(response.getBody().toString().contains("Fresh"));
    Assertions.assertTrue(response.getBody().toString().contains("Mozzarella"));
    verify(mockService).getIngredientsByRecipe(recipeId);
  }

  @Test
  public void testAddIngredientToRecipe() {
    Integer recipeId = 1;
    IngredientRequest requestBody = new IngredientRequest(1, 2, "kg", "Fresh");

    doNothing().when(mockService).addIngredientToRecipe(recipeId, 1, 2, "kg", "Fresh");

    HttpResponse<JsonNode> response = Unirest.post(BASE_URL + "/1/ingredients")
        .header("Content-Type", "application/json")
        .body(gson.toJson(requestBody))
        .asJson();

    Assertions.assertEquals(201, response.getStatus());
    Assertions.assertTrue(response.getBody().toString().contains("Ingredient added to recipe"));
    verify(mockService).addIngredientToRecipe(recipeId, 1, 2, "kg", "Fresh");
  }

  @Test
  public void testRemoveIngredientFromRecipe() {
    Integer recipeId = 1;
    Integer ingredientId = 1;

    doNothing().when(mockService).removeIngredientFromRecipe(recipeId, ingredientId);

    HttpResponse<JsonNode> response = Unirest.delete(BASE_URL + "/1/ingredients/1").asJson();

    Assertions.assertEquals(200, response.getStatus());
    Assertions.assertTrue(response.getBody().toString().contains("Ingredient deleted from recipe"));
    verify(mockService).removeIngredientFromRecipe(recipeId, ingredientId);
  }

  @Test
  public void testRemoveAllIngredientsFromRecipe() {
    Integer recipeId = 1;

    doNothing().when(mockService).removeAllIngredientsFromRecipe(recipeId);

    HttpResponse<JsonNode> response = Unirest.delete(BASE_URL + "/1/ingredients").asJson();

    Assertions.assertEquals(200, response.getStatus());
    Assertions.assertTrue(response.getBody().toString().contains("Delete all ingredients from recipe"));
    verify(mockService).removeAllIngredientsFromRecipe(recipeId);
  }

  /** Local DTO class for testing */
  private record IngredientRequest(Integer ingredientId, Integer quantity, String unit, String note) {}

}

