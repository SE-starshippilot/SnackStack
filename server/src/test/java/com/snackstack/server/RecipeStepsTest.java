package com.snackstack.server;
import com.google.gson.Gson;
import com.snackstack.server.controller.RecipeStepsController;
import com.snackstack.server.model.RecipeSteps;
import com.snackstack.server.service.RecipeStepsService;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;

import java.util.List;

public class RecipeStepsTest {

  private static final int PORT = 4567;
  private static final String BASE_URL = "http://localhost:" + PORT + "/api/recipes";
  private static RecipeStepsService mockService;
  private static Gson gson;

  @BeforeAll
  public static void setup() throws Exception {
    mockService = mock(RecipeStepsService.class);
    gson = new Gson();
    RecipeStepsController controller = new RecipeStepsController(mockService, gson);
    controller.registerRoutes();

    // Wait for Spark to start
    Thread.sleep(500);
  }

  @AfterAll
  public static void tearDown() {
    spark.Spark.stop();
  }

  @Test
  public void testGetSteps() {
    Integer recipeId = 1;
    // Integer stepId,
    // Integer recipeId,
    // Integer stepNumber,
    // String stepDescription
    List<RecipeSteps> mockSteps = List.of(
        new RecipeSteps(1, 1, 4, "hello!"),
        new RecipeSteps(2, 1, 4, "love cooking!")
    );

    when(mockService.getStepsByRecipeId(recipeId)).thenReturn(mockSteps);

    HttpResponse<JsonNode> response = Unirest.get(BASE_URL + "/1/steps").asJson();

    Assertions.assertEquals(200, response.getStatus());
    Assertions.assertTrue(response.getBody().toString().contains("hello"));
    Assertions.assertTrue(response.getBody().toString().contains("love cooking!"));
    verify(mockService).getStepsByRecipeId(recipeId);
  }

  @Test
  public void testAddStep() {
    Integer recipeId = 1;
    StepRequest requestBody = new StepRequest(3, "Bake for 30 minutes");

    doNothing().when(mockService).addStep(recipeId, 3, "Bake for 30 minutes");

    HttpResponse<JsonNode> response = Unirest.post(BASE_URL + "/1/steps")
        .header("Content-Type", "application/json")
        .body(gson.toJson(requestBody))
        .asJson();

    Assertions.assertEquals(201, response.getStatus());
    Assertions.assertTrue(response.getBody().toString().contains("Step added to recipe"));
    verify(mockService).addStep(recipeId, 3, "Bake for 30 minutes");
  }

  @Test
  public void testDeleteStep() {
    Integer recipeId = 1;
    Integer stepNumber = 2;

    doNothing().when(mockService).deleteStep(recipeId, stepNumber);

    HttpResponse<JsonNode> response = Unirest.delete(BASE_URL + "/1/steps/2").asJson();

    Assertions.assertEquals(200, response.getStatus());
    Assertions.assertTrue(response.getBody().toString().contains("A step deleted from recipe"));
    verify(mockService).deleteStep(recipeId, stepNumber);
  }

  @Test
  public void testDeleteAllSteps() {
    Integer recipeId = 1;

    doNothing().when(mockService).deleteAllSteps(recipeId);

    HttpResponse<JsonNode> response = Unirest.delete(BASE_URL + "/1/steps").asJson();

    Assertions.assertEquals(200, response.getStatus());
    Assertions.assertTrue(response.getBody().toString().contains("All steps deleted from recipe"));
    verify(mockService).deleteAllSteps(recipeId);
  }

  /** Local DTO class for testing */
  private record StepRequest(Integer stepNumber, String stepDescription) {}

}

