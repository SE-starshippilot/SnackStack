package com.snackstack.server.controller;

import com.google.gson.Gson;
import com.snackstack.server.model.Ingredient;
import com.snackstack.server.service.IngredientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

import static spark.Spark.*;

public class IngredientController {
  private static final Logger logger = LoggerFactory.getLogger(IngredientController.class);

  /**
   * Register all ingredient routes:
   *
   *   • GET    /api/ingredients
   *   • GET    /api/ingredients/:id
   *   • POST   /api/ingredients
   *   • PUT    /api/ingredients/:id
   *   • DELETE /api/ingredients/:id
   */
  public static void registerRoutes(IngredientService svc, Gson gson) {

    logger.info("Registering Ingredient API routes");

    /** ---------- Error Mapping ---------- */
    exception(Exception.class, (ex, req, res) -> {
      res.status(500);
      res.body(gson.toJson(new ErrorBody("Server error: " + ex.getMessage())));
      logger.error("Unhandled exception", ex);
    });

    /** ---------- Ingredient Resource ---------- */
    path("/api/ingredients", () -> {

      // GET /api/ingredients
      get("", (req, res) -> {
        logger.info("GET all ingredients");
        List<Ingredient> ingredients = svc.getAllIngredients();
        res.status(200);
        return gson.toJson(ingredients);
      });

      // GET /api/ingredients/:id
      get("/:id", (req, res) -> {
        int id = Integer.parseInt(req.params(":id"));
        logger.info("GET ingredient by ID: {}", id);
        Ingredient ingredient = svc.getIngredientById(id);

        if (ingredient == null) {
          res.status(404);
          return gson.toJson(new ErrorBody("Ingredient not found"));
        }

        res.status(200);
        return gson.toJson(ingredient);
      });

      // POST /api/ingredients
      post("", (req, res) -> {
        IngredientRequest body = gson.fromJson(req.body(), IngredientRequest.class);
        logger.info("POST create new ingredient: {}", body.ingredientName());

        long id = svc.createIngredient(body.ingredientId(), body.ingredientName());

        res.status(201);
        return gson.toJson(new SuccessBody("Ingredient created", Instant.now().toString(), id));
      });

      // PUT /api/ingredients/:id
      put("/:id", (req, res) -> {
        int id = Integer.parseInt(req.params(":id"));
        IngredientRequest body = gson.fromJson(req.body(), IngredientRequest.class);
        logger.info("PUT update ingredient ID {} to name '{}'", id, body.ingredientName());

        svc.updateIngredient(id, body.ingredientName());

        res.status(200);
        return gson.toJson(new SuccessBody("Ingredient updated", Instant.now().toString(), id));
      });

      // DELETE /api/ingredients/:id
      delete("/:id", (req, res) -> {
        int id = Integer.parseInt(req.params(":id"));
        logger.info("DELETE ingredient ID {}", id);

        svc.deleteIngredientById(id);
        res.status(200);
        return gson.toJson(new SuccessBody("Ingredient deleted", Instant.now().toString(), id));
      });
    });

    logger.info("Ingredient API routes registered successfully");
  }

  /** ---------- Request / Response DTOs ---------- */
  private record IngredientRequest(Integer ingredientId, String ingredientName) {}
  private record SuccessBody(String message, String at, long id) {}
  private record ErrorBody(String error) {}

}
