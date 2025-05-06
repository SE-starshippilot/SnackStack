package com.snackstack.server.controller;

import com.google.gson.Gson;
import com.snackstack.server.model.RecipeSteps;
import com.snackstack.server.service.RecipeStepsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

import static spark.Spark.*;

public final class RecipeStepsController {

  private static final Logger logger = LoggerFactory.getLogger(RecipeStepsController.class);

  public static void registerRoutes(RecipeStepsService svc, Gson gson) {
    logger.info("Registering RecipeSteps API routes");

    path("/api/recipes/:recipeId/steps", () -> {

      /** ---------- GET /api/recipes/{recipeId}/steps ---------- */
      get("", (req, res) -> {
        Integer recipeId = Integer.parseInt(req.params(":recipeId"));
        logger.info("GET steps for recipe {}", recipeId);

        List<RecipeSteps> steps = svc.getStepsByRecipeId(recipeId);
        res.status(200);
        return gson.toJson(steps);
      });

      /** ---------- POST /api/recipes/{recipeId}/steps ---------- */
      post("", (req, res) -> {
        Integer recipeId = Integer.parseInt(req.params(":recipeId"));
        StepRequest body = gson.fromJson(req.body(), StepRequest.class);

        logger.info("POST add step {} to recipe {}", body.stepNumber(), recipeId);

        svc.addStep(recipeId, body.stepNumber(), body.stepDescription());

        res.status(201);
        return gson.toJson(new SuccessBody("Step added to recipe", Instant.now().toString()));
      });

      /** ---------- DELETE /api/recipes/{recipeId}/steps/{stepNumber} ---------- */
      delete("/:stepNumber", (req, res) -> {
        Integer recipeId   = Integer.parseInt(req.params(":recipeId"));
        Integer stepNumber = Integer.parseInt(req.params(":stepNumber"));

        logger.info("DELETE step {} from recipe {}", stepNumber, recipeId);
        svc.deleteStep(recipeId, stepNumber);

        res.status(200);
        return gson.toJson(new SuccessBody("A step deleted from recipe", Instant.now().toString()));
      });

      /** ---------- DELETE /api/recipes/{recipeId}/steps ---------- */
      delete("", (req, res) -> {
        Integer recipeId = Integer.parseInt(req.params(":recipeId"));

        logger.info("DELETE all steps for recipe {}", recipeId);
        svc.deleteAllSteps(recipeId);

        res.status(200);
        return gson.toJson(new SuccessBody("All steps deleted from recipe", Instant.now().toString()));
      });
    });

    logger.info("RecipeSteps API routes registered");
  }

  /** ---------- Local DTOs ---------- */
  private record StepRequest(Integer stepNumber, String stepDescription) {}
  private record SuccessBody(String message, String at) {}
}
