package com.snackstack.server.dao;

import com.snackstack.server.dto.RecipeHistoryDTO;
import com.snackstack.server.dto.IngredientDTO;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

@RegisterConstructorMapper(RecipeHistoryDTO.class)
@RegisterConstructorMapper(IngredientDTO.class)
public interface RecipeHistoryDAO {
    @SqlUpdate("INSERT INTO recipe_history (user_id, recipe_id, created_at) VALUES (:userId, :recipeId, :createdAt)")
    @GetGeneratedKeys("history_id")
    int insertHistory(@Bind("userId") int userId, @Bind("recipeId") int recipeId, @Bind("createdAt") Instant createdAt);

    @SqlUpdate("DELETE FROM recipe_history WHERE history_id = :historyId AND user_id = :userId")
    int deleteHistoryById(@Bind("historyId") int historyId, @Bind("userId") int userId);

    @SqlQuery("""
        SELECT h.history_id as id, h.user_id, h.recipe_id, 
               r.recipe_name as recipeName, r.description as recipeDescription, 
               h.created_at as createdAt, r.is_favorite as isFavorite
        FROM recipe_history h
        JOIN recipes r ON h.recipe_id = r.recipe_id
        WHERE h.history_id = :historyId AND h.user_id = :userId
    """)
    Optional<RecipeHistoryDTO> getHistoryById(@Bind("historyId") int historyId, @Bind("userId") int userId);

    @SqlQuery("""
        SELECT h.history_id as id, h.user_id, h.recipe_id, 
               r.recipe_name as recipeName, r.description as recipeDescription, 
               h.created_at as createdAt, r.is_favorite as isFavorite
        FROM recipe_history h
        JOIN recipes r ON h.recipe_id = r.recipe_id
        WHERE h.user_id = :userId
        AND (:favoriteOnly = false OR r.is_favorite = true)
        AND (:searchTerm IS NULL OR LOWER(r.recipe_name) LIKE CONCAT('%', LOWER(:searchTerm), '%'))
        ORDER BY h.created_at DESC NULLS LAST
        LIMIT :limit OFFSET :offset
    """)
    List<RecipeHistoryDTO> getHistoryByUserId(
        @Bind("userId") int userId,
        @Bind("offset") int offset,
        @Bind("limit") int limit,
        @Bind("favoriteOnly") boolean favoriteOnly,
        @Bind("searchTerm") String searchTerm,
        @Bind("sortAsc") boolean sortAsc
    );

    @SqlQuery("""
        SELECT step_description
        FROM recipe_steps
        WHERE recipe_id = :recipeId
        ORDER BY step_number
    """)
    List<String> getRecipeSteps(@Bind("recipeId") int recipeId);

    @SqlQuery("""
        SELECT i.ingredient_name, ri.quantity, ri.unit, ri.note
        FROM recipe_ingredients ri
        JOIN ingredients i ON ri.ingredient_id = i.ingredient_id
        WHERE ri.recipe_id = :recipeId
    """)
    List<IngredientDTO> getIngredientsForRecipe(@Bind("recipeId") int recipeId);
}