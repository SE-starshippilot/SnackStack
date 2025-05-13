package com.snackstack.server;

import static org.mockito.Mockito.*;

import com.google.gson.Gson;
import com.snackstack.server.controller.InventoryController;
import com.snackstack.server.service.InventoryService;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.junit.jupiter.api.*;

import java.util.List;

public class InventoryTest {

  private static final int TEST_PORT = 4567;
  private static final String BASE_URL = "http://localhost:" + TEST_PORT + "/api/users/testUser/inventory";

  private static InventoryService mockService;
  private static Gson gson;

  @BeforeAll
  public static void setup() throws Exception {
    mockService = mock(InventoryService.class);
    gson = new Gson();

    InventoryController controller = new InventoryController(mockService, gson);
    controller.registerRoutes();
    Thread.sleep(500); // Wait for Spark to initialize
  }

  @AfterAll
  public static void tearDown() {
    spark.Spark.stop();
  }

  @Test
  public void testGetInventory() {
    when(mockService.getIngredients("testUser")).thenReturn(List.of("apple", "banana"));

    HttpResponse<JsonNode> response = Unirest.get(BASE_URL).asJson();

    Assertions.assertEquals(200, response.getStatus());
    Assertions.assertTrue(response.getBody().toString().contains("apple"));
    verify(mockService).getIngredients("testUser");
  }

  @Test
  public void testPostInventory() {
    String ingredient = "orange";
    String jsonBody = gson.toJson(new InventoryRequest(ingredient));

    HttpResponse<JsonNode> response = Unirest.post(BASE_URL)
        .header("Content-Type", "application/json")
        .body(jsonBody)
        .asJson();

    Assertions.assertEquals(201, response.getStatus());
    Assertions.assertTrue(response.getBody().toString().contains("Ingredient added"));
    verify(mockService).addInventoryRecord("testUser", ingredient);
  }

  @Test
  public void testDeleteInventorySuccess() {
    when(mockService.deleteIngredient("testUser", "apple")).thenReturn(1);

    HttpResponse<String> response = Unirest.delete(BASE_URL + "/apple").asString();

    Assertions.assertEquals(204, response.getStatus());
    verify(mockService).deleteIngredient("testUser", "apple");
  }

  @Test
  public void testDeleteInventoryNotFound() {
    when(mockService.deleteIngredient("testUser", "nonexistent")).thenReturn(0);

    HttpResponse<JsonNode> response = Unirest.delete(BASE_URL + "/nonexistent").asJson();

    Assertions.assertEquals(404, response.getStatus());
    Assertions.assertTrue(response.getBody().toString().contains("Ingredient not found"));
    verify(mockService).deleteIngredient("testUser", "nonexistent");
  }

  // construct POST request body
  private record InventoryRequest(String ingredientName) {}
}

