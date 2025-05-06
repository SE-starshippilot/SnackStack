package com.snackstack.server.controller;

import com.google.gson.Gson;
import com.snackstack.server.model.Recipe;
import com.snackstack.server.service.RecipesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.Arrays;

import static spark.Spark.*;

public final class RecipesController {

  private static final Logger logger = LoggerFactory.getLogger(RecipesController.class);

  public static void registerRoutes(RecipesService svc, Gson gson) {

    logger.info("Registering Recipe API routes");

    path("/api/recipes", () -> {

      /** ---------- GET /api/recipes ---------- */
      get("", (req, res) -> {
        logger.info("GET all recipes");
        List<Recipe> recipes = svc.getAllRecipes();
        res.status(200);
        return gson.toJson(recipes);
      });

      /** ---------- POST /api/recipes ---------- */
      post("", (req, res) -> {
        RecipeRequest body = gson.fromJson(req.body(), RecipeRequest.class);
        logger.info("POST create new recipe: {}", body.recipeName());

        Integer recipeId = svc.createRecipe(
            body.recipeName(),
            body.description(),
            body.servings(),
            body.recipeOriginId(),
            body.recipeType()
        );

        res.status(201);
        return gson.toJson(new SuccessBody(
            "Recipe created",
            Instant.now().toString(),
            recipeId
        ));
      });

      /** ---------- DELETE /api/recipes/:recipeId ---------- */
      delete("/:recipeId", (req, res) -> {
        Integer recipeId = Integer.parseInt(req.params(":recipeId"));
        logger.info("DELETE recipe with ID {}", recipeId);
        svc.deleteRecipe(recipeId);
        res.status(200);
        return gson.toJson(new SuccessBody(
            "Recipe deleted",
            Instant.now().toString(),
            recipeId
        ));
      });

      /** ---------- DELETE /api/recipes ---------- */
      delete("", (req, res) -> {
        // get recipeIds array
        Integer[] recipeIds = gson.fromJson(req.body(), Integer[].class);
        logger.info("DELETE recipes with ids: {}", Arrays.toString(recipeIds));

        try {
          svc.deleteRecipesByIds(recipeIds);
          res.status(200);
          return gson.toJson(new SuccessBody(
              "Recipe deleted with some ids",
              Instant.now().toString(),
              0
          ));
        } catch (Exception e) {
          res.status(500);
          return gson.toJson(new ErrorBody("Error deleting recipes"));
        }
      });
    });

    logger.info("Recipe API routes registered");
  }

  /** ---------- Local DTOs ---------- */
  private record RecipeRequest(
      String recipeName,
      String description,
      Integer servings,
      String recipeOriginId,
      String recipeType
  ) {}

  private record SuccessBody(String message, String at, Integer recipeId) {}

  private record ErrorBody(String error) {}
}
