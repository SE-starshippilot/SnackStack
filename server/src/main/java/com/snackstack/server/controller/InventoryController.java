package com.snackstack.server.controller;

import static spark.Spark.*;

import com.google.gson.Gson;
import com.snackstack.server.exceptions.RecordNotFound;
import com.snackstack.server.service.InventoryService;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InventoryController implements Controller {

  private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
  private final InventoryService service;
  private final Gson gson;

  public InventoryController(InventoryService service, Gson gson) {
    this.service = service;
    this.gson = gson;
  }

  @Override
  public String getBasePath() {
    return "/api/users/:userName/inventory";
  }

  @Override
  public void registerRoutes() {
    logger.info("Registering Inventory API routes");

    /* ---------- Error mapping (RecordNotFound → 404) ---------- */
    exception(RecordNotFound.class, (ex, req, res) -> {
      res.status(404);
      res.body(gson.toJson(new ErrorBody(ex.getMessage())));
    });

    /* ---------- Inventory sub-resource ---------- */
    path(getBasePath(), () -> {

      /* ---- GET /api/users/{userName}/inventory ---- */
      get("", (req, res) -> {
        String userName = req.params(":userName");
        logger.info("GET inventory for user '{}'", userName);

        List<String> items = service.getIngredients(userName);
        res.status(200);
        return gson.toJson(items);
      });

      /* ---- POST /api/users/{userName}/inventory ---- */
      post("", (req, res) -> {
        String userName = req.params(":userName");
        InventoryRequest body = gson.fromJson(req.body(), InventoryRequest.class);

        logger.info("POST add ingredient '{}' for user '{}'", body.ingredientName(), userName);

        service.createIngredient(userName, body.ingredientName());

        res.status(201);
        return gson.toJson(new SuccessBody(
            "Ingredient added",
            Instant.now().toString()));
      });

      /* ---- DELETE /api/users/{userName}/inventory/{ingredientName} ---- */
      delete("/:ingredientName", (req, res) -> {
        String userName = req.params(":userName");
        String ingredientName = req.params(":ingredientName");

        logger.info("DELETE ingredient '{}' for user '{}'", ingredientName, userName);

        int deleted = service.deleteIngredient(userName, ingredientName);
        if (deleted == 0) {
          res.status(404);
          return gson.toJson(new ErrorBody("Ingredient not found"));
        }
        res.status(204);
        return "";
      });
    });

    logger.info("Inventory API routes registered successfully");
  }

  /* ---------- tiny request / response DTOs local to controller ---------- */
  private record InventoryRequest(String ingredientName) {

  }

  private record SuccessBody(String message, String at) {

  }

  private record ErrorBody(String error) {

  }
}