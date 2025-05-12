package com.snackstack.server.controller;

import static spark.Spark.*;

import com.google.gson.Gson;
import com.snackstack.server.dto.RecipeRequestDTO;
import com.snackstack.server.dto.RecipeResponseDTO;
import com.snackstack.server.service.RecipeService;
import com.snackstack.server.exceptions.InvalidIngredientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class RecipeController implements Controller {

  public static final Logger logger = LoggerFactory.getLogger(RecipeController.class);
  private final RecipeService service;
  private final Gson gson;

  public RecipeController(RecipeService service, Gson gson) {
    this.service = service;
    this.gson = gson;
  }

  @Override
  public void registerRoutes() {
    logger.info("Registering Recipe API routes");

    exception(InvalidIngredientException.class, (ex, req, res) -> {
      res.status(400);
      res.type("application/json");
      res.body(gson.toJson(new ErrorResponse(ex.getMessage())));
    });

    path(getBasePath(), () -> {
      post("", (req, res) -> {
        logger.info("Received recipe generation request");
        
        res.type("application/json");
        
        try {
          RecipeRequestDTO request = gson.fromJson(req.body(), RecipeRequestDTO.class);
          if (request == null) {
            res.status(400);
            return gson.toJson(new ErrorResponse("Invalid request body"));
          }
          
          List<RecipeResponseDTO> recipes = service.generateRecipes(request);
          
          res.status(200);
          logger.info("Generated {} recipes", recipes.size());
          return gson.toJson(new SuccessResponse(recipes));
          
        } catch (Exception e) {
          logger.error("Error processing recipe generation request", e);
          res.status(500);
          return gson.toJson(new ErrorResponse("Internal server error: " + e.getMessage()));
        }
      });
    });

    logger.info("Recipe API routes registered successfully");
  }

  @Override
  public String getBasePath() {
    return "/api/recipes/recipe";
  }

  private record ErrorResponse(String error) {}
  private record SuccessResponse(List<RecipeResponseDTO> recipes) {}
}
