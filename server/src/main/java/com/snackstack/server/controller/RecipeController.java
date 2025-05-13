package com.snackstack.server.controller;

import static spark.Spark.*;

import com.google.gson.Gson;
import com.snackstack.server.dto.RecipeRequestDTO;
import com.snackstack.server.dto.RecipeResponseDTO;
import com.snackstack.server.exceptions.InvalidIngredientException;
import com.snackstack.server.service.RecipeService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeController implements Controller {

  private static final Logger logger = LoggerFactory.getLogger(RecipeController.class);
  private final RecipeService service;
  private final Gson gson;

  public RecipeController(RecipeService service, Gson gson) {
    this.service = service;
    this.gson = gson;
  }

  @Override
  public String getBasePath() {
    return "/api/recipes";
  }

  @Override
  public void registerRoutes() {
    logger.info("Registering Recipe API routes");

    path(getBasePath(), () -> {

      /* =====================================================
         POST /api/recipes/user/:userId
         Body: { servings, recipeType, recipeOrigin, allergies }
         ===================================================== */
      post("/user/:userId", (req, res) -> {       // <<< changed route
        res.type("application/json");

        /* ----- 1.  Parse path param ----- */
        String userIdStr = req.params(":userId");
        Integer userId;
        try {
          userId = Integer.valueOf(userIdStr);
        } catch (NumberFormatException e) {
          res.status(400);
          return gson.toJson(new ErrorResponse("userId must be an integer"));
        }

        logger.info("Generating recipes for user {}", userId);

        /* ----- 2.  Parse JSON body directly into the public DTO ----- */
        RecipeRequestDTO apiReq =
            gson.fromJson(req.body(), RecipeRequestDTO.class);

        if (apiReq == null) {
          res.status(400);
          return gson.toJson(new ErrorResponse("Request body is missing or invalid JSON"));
        }
        if (apiReq.recipeType() == null) {
          res.status(400);
          return gson.toJson(new ErrorResponse("recipeType is required"));
        }

        /* ----- 3.  Delegate to service ----- */
        try {
          List<RecipeResponseDTO> recipes =
              service.generateRecipeForUser(userId, apiReq);  // your renamed service method

          res.status(201);
          return gson.toJson(recipes);
        } catch (IllegalArgumentException | InvalidIngredientException e) {
          res.status(400);
          return gson.toJson(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
          logger.error("Recipe generation failed", e);
          res.status(500);
          return gson.toJson(new ErrorResponse("Internal server error"));
        }
      });

      /* ---- add any other recipe routes here ---- */
    });

    logger.info("Recipe API routes registered successfully");
  }

  /* small record used for consistent error envelopes */
  private record ErrorResponse(String message) {}
}
