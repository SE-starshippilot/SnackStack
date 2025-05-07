package com.snackstack.server.controller;

import com.google.gson.Gson;
import com.snackstack.server.model.RecipeIngredient;
import com.snackstack.server.service.RecipeIngredientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

import static spark.Spark.*;

public final class RecipeIngredientController implements Controller {

  private static final Logger logger = LoggerFactory.getLogger(RecipeIngredientController.class);
  private final RecipeIngredientService svc;
  private final Gson gson;

  public RecipeIngredientController(RecipeIngredientService svc, Gson gson) {
    this.svc = svc;
    this.gson = gson;
  }

  @Override
  public String getBasePath() {
    return "/api/recipes/:recipeId/ingredients";
  }

  @Override
  public void registerRoutes() {

    logger.info("Registering RecipeIngredient API routes");

    path("/api/recipes/:recipeId/ingredients", () -> {

      /** ---------- GET /api/recipes/{recipeId}/ingredients ---------- */
      get("", (req, res) -> {
        Integer recipeId = Integer.parseInt(req.params(":recipeId"));
        logger.info("GET ingredients for recipe {}", recipeId);

        List<RecipeIngredient> ingredients = svc.getIngredientsByRecipe(recipeId);
        res.status(200);
        return gson.toJson(ingredients);
      });

      /** ---------- POST /api/recipes/{recipeId}/ingredients ---------- */
      post("", (req, res) -> {
        Integer recipeId = Integer.parseInt(req.params(":recipeId"));
        IngredientRequest body = gson.fromJson(req.body(), IngredientRequest.class);

        logger.info("POST add ingredient {} to recipe {}", body.ingredientId(), recipeId);

        svc.addIngredientToRecipe(recipeId, body.ingredientId(), body.quantity(), body.unit(), body.note());

        res.status(201);
        return gson.toJson(new SuccessBody(
            "Ingredient added to recipe",
            Instant.now().toString()
        ));
      });

      /** ---------- DELETE /api/recipes/{recipeId}/ingredients/{ingredientId} ---------- */
      delete("/:ingredientId", (req, res) -> {
        Integer recipeId = Integer.parseInt(req.params(":recipeId"));
        Integer ingredientId = Integer.parseInt(req.params(":ingredientId"));

        logger.info("DELETE ingredient {} from recipe {}", ingredientId, recipeId);
        svc.removeIngredientFromRecipe(recipeId, ingredientId);

        res.status(200);
        return gson.toJson(new SuccessBody(
            "Ingredient deleted from recipe",
            Instant.now().toString()
        ));
      });

      /** ---------- DELETE /api/recipes/{recipeId}/ingredients ---------- */
      delete("", (req, res) -> {
        Integer recipeId = Integer.parseInt(req.params(":recipeId"));
        logger.info("DELETE all ingredients from recipe {}", recipeId);

        svc.removeAllIngredientsFromRecipe(recipeId);
        res.status(200);
        return gson.toJson(new SuccessBody(
            "Delete all ingredients from recipe",
            Instant.now().toString()
        ));
      });
    });

    logger.info("RecipeIngredient API routes registered successfully");
  }

  /** ---------- Local DTOs ---------- */
  private record IngredientRequest(
      Integer ingredientId,
      Integer quantity,
      String unit,
      String note
  ) {}

  private record SuccessBody(String message, String at) {}
}
