package com.snackstack.server.dto;

import java.time.Instant;
import java.util.List;
import org.jdbi.v3.core.mapper.reflect.JdbiConstructor;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

public record RecipeHistoryDTO(
    @ColumnName("id") int id,
    @ColumnName("user_id") int userId,
    @ColumnName("recipe_id") int recipeId,
    @ColumnName("recipeName") String recipeName,
    @ColumnName("recipeDescription") String recipeDescription,
    @ColumnName("createdAt") Instant createdAt,
    @ColumnName("isFavorite") boolean isFavorite,
    List<String> recipeStep,
    List<IngredientDTO> recipeIngredients
) {
    @JdbiConstructor
    public RecipeHistoryDTO(
        @ColumnName("id") int id,
        @ColumnName("user_id") int userId,
        @ColumnName("recipe_id") int recipeId,
        @ColumnName("recipeName") String recipeName,
        @ColumnName("recipeDescription") String recipeDescription,
        @ColumnName("createdAt") Instant createdAt,
        @ColumnName("isFavorite") boolean isFavorite
    ) {
        this(id, userId, recipeId, recipeName, recipeDescription, createdAt, isFavorite, null, null);
    }
} 