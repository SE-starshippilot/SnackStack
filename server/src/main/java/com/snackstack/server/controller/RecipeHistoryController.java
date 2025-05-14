package com.snackstack.server.controller;

import static spark.Spark.*;

import com.google.gson.Gson;
import com.snackstack.server.dto.RecipeHistoryDTO;
import com.snackstack.server.service.RecipeHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class RecipeHistoryController implements Controller {
    private static final Logger logger = LoggerFactory.getLogger(RecipeHistoryController.class);
    private final RecipeHistoryService service;
    private final Gson gson;

    public RecipeHistoryController(RecipeHistoryService service, Gson gson) {
        this.service = service;
        this.gson = gson;
    }

    @Override
    public String getBasePath() {
        return "/api/history";
    }

    @Override
    public void registerRoutes() {
        logger.info("Registering Recipe History API routes");

        path(getBasePath(), () -> {
            get("/:userId", (req, res) -> {
                int userId = Integer.parseInt(req.params(":userId"));
                int offset = req.queryParams("offset") != null ? Integer.parseInt(req.queryParams("offset")) : 0;
                int limit = req.queryParams("limit") != null ? Integer.parseInt(req.queryParams("limit")) : 10;
                boolean sortAsc = req.queryParams("sortAsc") != null ? Boolean.parseBoolean(req.queryParams("sortAsc")) : true;
                boolean favoriteOnly = req.queryParams("favoriteOnly") != null ? Boolean.parseBoolean(req.queryParams("favoriteOnly")) : false;
                String searchTerm = req.queryParams("keyword");
                
                logger.info("Received request to get recipe history for user: {}", userId);
                
                List<RecipeHistoryDTO> history = service.getUserRecipeHistory(
                    userId, offset, limit, sortAsc, favoriteOnly, searchTerm
                );
                logger.info("Retrieved {} history entries for user {}", history.size(), userId);
                return gson.toJson(history);
            });

            post("", (req, res) -> {
                var body = gson.fromJson(req.body(), HistoryRequest.class);
                logger.info("Received request to add recipe uuid {} to history for user {}",
                    body.recipeUuid(), body.userId());
                
                int historyId = service.addToHistory(body.userId(), body.recipeUuid());
                res.status(201);
                logger.info("Recipe added to history with ID: {}", historyId);
                return gson.toJson(new IdResponse(historyId));
            });

            delete("/:historyId", (req, res) -> {
                int historyId = Integer.parseInt(req.params(":historyId"));
                int userId = Integer.parseInt(req.queryParams("userId"));
                logger.info("Received request to delete history entry {} for user {}", historyId, userId);

                service.deleteHistoryEntry(historyId, userId);
                res.status(204);
                logger.info("History entry deleted successfully");
                return "";
            });

            get("/:historyId/details", (req, res) -> {
                int historyId = Integer.parseInt(req.params(":historyId"));
                int userId = Integer.parseInt(req.queryParams("userId"));
                logger.info("Received request to get details for history entry {} for user {}", 
                    historyId, userId);

                RecipeHistoryDTO recipe = service.getHistoryEntry(historyId, userId);
                logger.info("Retrieved details for history entry {}", historyId);
                return gson.toJson(recipe);
            });
        });

        logger.info("Recipe History API routes registered successfully");
    }

    private record HistoryRequest(int userId, String recipeUuid) {}
    private record IdResponse(int id) {}
} 