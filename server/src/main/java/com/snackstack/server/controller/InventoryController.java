package com.snackstack.server.controller;

import static spark.Spark.*;

import com.google.gson.Gson;
import com.snackstack.server.exceptions.RecordNotFound;
import com.snackstack.server.service.InventoryService;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class InventoryController {

  private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

  /**
   * Register all inventory routes.
   *
   *   • GET    /api/users/:userName/inventory
   *   • POST   /api/users/:userName/inventory
   *   • DELETE /api/users/:userName/inventory/:ingredientName
   */
  public static void registerRoutes(InventoryService svc, Gson gson) {

    logger.info("Registering Inventory API routes");

    /** ---------- Error mapping (RecordNotFound → 404) ---------- */
    exception(RecordNotFound.class, (ex, req, res) -> {
      res.status(404);
      res.body(gson.toJson(new ErrorBody(ex.getMessage())));
    });

    /** ---------- Inventory sub-resource ---------- */
    path("/api/users/:userName/inventory", () -> {

      /* ---- GET /api/users/{userName}/inventory ---- */
      get("", (req, res) -> {
        String userName = req.params(":userName");
        logger.info("GET inventory for user '{}'", userName);

        List<String> items = svc.getIngredients(userName);
        res.status(200);
        return gson.toJson(items);
      });

      /* ---- POST /api/users/{userName}/inventory ---- */
      post("", (req, res) -> {
        String userName = req.params(":userName");
        InventoryRequest body = gson.fromJson(req.body(), InventoryRequest.class);

        logger.info("POST add ingredient '{}' for user '{}'", body.ingredientName(), userName);

        // (Optional) you might accept quantity/unit in body and pass them to the service later
        svc.createIngredient(userName, body.ingredientName());

        res.status(201);
        return gson.toJson(new SuccessBody(
            "Ingredient added",
            Instant.now().toString()));
      });

      /* ---- DELETE /api/users/{userName}/inventory/{ingredientName} ---- */
      delete("/:ingredientName", (req, res) -> {
        String userName       = req.params(":userName");
        String ingredientName = req.params(":ingredientName");

        logger.info("DELETE ingredient '{}' for user '{}'", ingredientName, userName);

        int deleted = svc.deleteIngredient(userName, ingredientName);
        if (deleted == 0) {                    // Shouldn’t occur; svc throws on “user not found”
          res.status(404);
          return gson.toJson(new ErrorBody("Ingredient not found"));
        }
        res.status(204);                       // No body on success
        return "";
      });
    });

    logger.info("Inventory API routes registered successfully");
  }

  /* ---------- tiny request / response DTOs local to controller ---------- */
  private record InventoryRequest(String ingredientName) {}
  private record SuccessBody(String message, String at) {}
  private record ErrorBody(String error) {}
}
