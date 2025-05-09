package com.snackstack.server;

import com.google.gson.Gson;
import com.snackstack.server.controller.RecipesController;
import com.snackstack.server.model.Recipe;
import com.snackstack.server.service.RecipesService;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.mockito.Mockito.*;

public class RecipeTest {

  private static final int PORT = 4567;
  private static final String BASE_URL = "http://localhost:" + PORT + "/api/recipes";

  private static RecipesService mockService;
  private static Gson gson;

  @BeforeAll
  public static void setup() throws Exception {
    mockService = mock(RecipesService.class);
    gson = new Gson();
    RecipesController controller = new RecipesController(mockService, gson);
    controller.registerRoutes();

    Thread.sleep(500);
  }

  @AfterAll
  public static void tearDown() {
    spark.Spark.stop();
  }

  @Test
  public void testGetAllRecipes() {
    List<Recipe> mockRecipes = List.of(
        new Recipe(1, "Pasta", "Tasty", 2, "IT", "Dinner"),
        new Recipe(2, "Soup", "Hot", 1, "CN", "Starter")
    );

    when(mockService.getAllRecipes()).thenReturn(mockRecipes);

    HttpResponse<JsonNode> response = Unirest.get(BASE_URL).asJson();

    Assertions.assertEquals(200, response.getStatus());
    Assertions.assertTrue(response.getBody().toString().contains("Pasta"));
    Assertions.assertTrue(response.getBody().toString().contains("Soup"));
    verify(mockService).getAllRecipes();
  }

  @Test
  public void testCreateRecipe() {
    // use argument matcher
    when(mockService.createRecipe(
        eq("Cake"), eq("Sweet"), eq(4), eq("FR"), eq("Dessert")
    )).thenReturn(99);

    RecipeRequest request = new RecipeRequest("Cake", "Sweet", 4, "FR", "Dessert");

    HttpResponse<JsonNode> response = Unirest.post(BASE_URL)
        .header("Content-Type", "application/json")
        .body(gson.toJson(request))
        .asJson();

    Assertions.assertEquals(201, response.getStatus());
    Assertions.assertTrue(response.getBody().toString().contains("Recipe created"));
    Assertions.assertTrue(response.getBody().toString().contains("99"));
    verify(mockService).createRecipe("Cake", "Sweet", 4, "FR", "Dessert");
  }

  @Test
  public void testDeleteSingleRecipe() {
    HttpResponse<JsonNode> response = Unirest.delete(BASE_URL + "/123").asJson();

    Assertions.assertEquals(200, response.getStatus());
    Assertions.assertTrue(response.getBody().toString().contains("Recipe deleted"));
    verify(mockService).deleteRecipe(123);
  }

  @Test
  public void testDeleteMultipleRecipesSuccess() {
    Integer[] ids = new Integer[]{1, 2, 3};
    // default behavior is do nothing after the method finished
    doNothing().when(mockService).deleteRecipesByIds(ids);

    HttpResponse<JsonNode> response = Unirest.delete(BASE_URL)
        .header("Content-Type", "application/json")
        .body(gson.toJson(ids))
        .asJson();

    Assertions.assertEquals(200, response.getStatus());
    Assertions.assertTrue(response.getBody().toString().contains("Recipe deleted with some ids"));
    verify(mockService).deleteRecipesByIds(ids);
  }

  @Test
  public void testDeleteMultipleRecipesFailure() {
    Integer[] ids = new Integer[]{100, 200};
    doThrow(new RuntimeException("DB error")).when(mockService).deleteRecipesByIds(ids);

    HttpResponse<JsonNode> response = Unirest.delete(BASE_URL)
        .header("Content-Type", "application/json")
        .body(gson.toJson(ids))
        .asJson();

    Assertions.assertEquals(500, response.getStatus());
    Assertions.assertTrue(response.getBody().toString().contains("Error deleting recipes"));
    verify(mockService).deleteRecipesByIds(ids);
  }

  /** 匹配 RecipeRequest JSON 格式 */
  private record RecipeRequest(String recipeName, String description, Integer servings, String recipeOriginId, String recipeType) {}
}

