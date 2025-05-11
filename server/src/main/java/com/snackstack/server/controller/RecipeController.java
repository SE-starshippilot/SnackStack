package com.snackstack.server.controller;

import com.google.gson.Gson;
import com.snackstack.server.service.RecipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  }

  @Override
  public String getBasePath() {
    return "/api/recipes/recipe";
  }
}
