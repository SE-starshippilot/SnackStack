package com.snackstack.server.service;

import com.snackstack.server.dao.RecipeDAO;
import com.snackstack.server.dao.RecipeHistoryDAO;
import com.snackstack.server.dto.RecipeHistoryDTO;
import com.snackstack.server.dto.IngredientDTO;
import com.snackstack.server.exceptions.RecordNotFound;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeHistoryService {
    private final RecipeHistoryDAO dao;
    private final RecipeDAO recipeDAO;
    private static final Logger logger = LoggerFactory.getLogger(RecipeHistoryService.class);

    public RecipeHistoryService(RecipeHistoryDAO dao, RecipeDAO recipeDAO) {
        this.dao = dao;
        this.recipeDAO = recipeDAO;
        logger.debug("RecipeHistoryService initialized");
    }

    public int addToHistory(int userId, String recipeUuid) {
        try {
            logger.info("Adding recipe with UUID {} to history for user {}", recipeUuid, userId);

            // Fetch the recipe ID using the UUID
            Integer recipeId = recipeDAO.getRecipeIdByUuid(recipeUuid);
            if (recipeId == null) {
                throw new RecordNotFound("Recipe not found with UUID: " + recipeUuid);
            }

            Instant now = Instant.now();
            int historyId = dao.insertHistory(userId, recipeId, now);
            logger.info("Successfully added recipe to history with ID: {}", historyId);
            return historyId;
        } catch (Exception e) {
            logger.error("Error adding recipe with UUID {} to history for user {}", recipeUuid, userId, e);
            throw e;
        }
    }

    public int deleteHistoryEntry(int historyId, int userId) {
        try {
            logger.info("Deleting history entry {} for user {}", historyId, userId);
            int rowsAffected = dao.deleteHistoryById(historyId, userId);
            if (rowsAffected == 0) {
                throw new RecordNotFound("History entry not found or does not belong to user");
            }
            logger.info("Successfully deleted history entry");
            return rowsAffected;
        } catch (Exception e) {
            logger.error("Error deleting history entry {} for user {}", historyId, userId, e);
            throw e;
        }
    }

    public RecipeHistoryDTO getHistoryEntry(int historyId, int userId) {
        try {
            logger.info("Fetching history entry {} for user {}", historyId, userId);
            Optional<RecipeHistoryDTO> history = dao.getHistoryById(historyId, userId);
            if (history.isEmpty()) {
                throw new RecordNotFound("History entry not found or does not belong to user");
            }
            List<String> steps = dao.getRecipeSteps(history.get().recipeId());
            List<IngredientDTO> ingredients = dao.getIngredientsForRecipe(history.get().recipeId());
            RecipeHistoryDTO enriched = new RecipeHistoryDTO(
                history.get().id(),
                history.get().userId(),
                history.get().recipeId(),
                history.get().recipeName(),
                history.get().recipeDescription(),
                history.get().createdAt(),
                history.get().isFavorite(),
                steps,
                ingredients
            );
            logger.info("Successfully retrieved history entry");
            return enriched;
        } catch (Exception e) {
            logger.error("Error fetching history entry {} for user {}", historyId, userId, e);
            throw e;
        }
    }

    public List<RecipeHistoryDTO> getUserRecipeHistory(
        int userId,
        int offset,
        int limit,
        boolean sortAsc,
        boolean favoriteOnly,
        String searchTerm
    ) {
        try {
            logger.info("Fetching recipe history for user: {} (offset={}, limit={}, favoriteOnly={}, search={}, sortAsc={})",
                userId, offset, limit, favoriteOnly, searchTerm, sortAsc);

            List<RecipeHistoryDTO> items = dao.getHistoryByUserId(
                userId, offset, limit, favoriteOnly, searchTerm, sortAsc
            );
            List<RecipeHistoryDTO> enrichedItems = new ArrayList<>();
            for (RecipeHistoryDTO item : items) {
                List<String> steps = dao.getRecipeSteps(item.recipeId());
                List<IngredientDTO> ingredients = dao.getIngredientsForRecipe(item.recipeId());
                enrichedItems.add(new RecipeHistoryDTO(
                    item.id(),
                    item.userId(),
                    item.recipeId(),
                    item.recipeName(),
                    item.recipeDescription(),
                    item.createdAt(),
                    item.isFavorite(),
                    steps,
                    ingredients
                ));
            }
            if (sortAsc) {
                Collections.reverse(enrichedItems);
            }
            logger.info("Retrieved {} items", enrichedItems.size());
            return enrichedItems;
        } catch (Exception e) {
            logger.error("Error fetching recipe history for user: {}", userId, e);
            throw e;
        }
    }
}