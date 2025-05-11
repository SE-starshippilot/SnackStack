package com.snackstack.server.dao;

import com.snackstack.server.model.RecipeHistory;
import java.util.List;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

/**
 * Data Access Object for the recipe_history table.
 * Provides methods to create, read, update, and delete recipe history records.
 * Uses JDBI SQL object API for database operations.
 */
@RegisterConstructorMapper(RecipeHistory.class)
public interface RecipeHistoryDAO {

  /*CREATE*/
  /**
   * Adds a new recipe history entry to the database.
   *
   * @param userId The ID of the user
   * @param recipeId The ID of the recipe
   * @return The ID of the newly created history entry
   */
  @SqlUpdate("""
      INSERT INTO recipe_history(user_id, recipe_id)
      VALUES (:userId, :recipeId)
      """)
  @GetGeneratedKeys("history_id")
  int addRecipeHistory(
      @Bind("userId") Integer userId,
      @Bind("recipeId") Integer recipeId
  );

  /*READ*/
  /**
   * Retrieves a single recipe history entry by its ID.
   *
   * @param historyId The ID of the history entry to retrieve
   * @return The RecipeHistory object or null if not found
   */
  @SqlQuery("""
      SELECT
        history_id as historyId,
        user_id as userId,
        recipe_id as recipeId,
        created_at as createdAt
      FROM recipe_history
      WHERE history_id = :id
      """)
  RecipeHistory getRecipeHistoryById(@Bind("id") Integer historyId);

  /**
   * Retrieves all recipe history entries from the database.
   *
   * @return A list of all RecipeHistory objects
   */
  @SqlQuery("""
      SELECT
        history_id as historyId,
        user_id as userId,
        recipe_id as recipeId,
        created_at as createdAt
      FROM recipe_history
      """)
  List<RecipeHistory> getAllRecipeHistory();

  /**
   * Retrieves all recipe history entries for a specific user.
   *
   * @param userId The ID of the user
   * @return A list of RecipeHistory objects for the specified user
   */
  @SqlQuery("""
      SELECT
        history_id as historyId,
        user_id as userId,
        recipe_id as recipeId,
        created_at as createdAt
      FROM recipe_history
      WHERE user_id = :userId
      ORDER BY created_at DESC
      """)
  List<RecipeHistory> getRecipeHistoryByUserId(@Bind("userId") Integer userId);

  /**
   * Retrieves all recipe history entries for a specific recipe.
   *
   * @param recipeId The ID of the recipe
   * @return A list of RecipeHistory objects for the specified recipe
   */
  @SqlQuery("""
      SELECT
        history_id as historyId,
        user_id as userId,
        recipe_id as recipeId,
        created_at as createdAt
      FROM recipe_history
      WHERE recipe_id = :recipeId
      ORDER BY created_at DESC
      """)
  List<RecipeHistory> getRecipeHistoryByRecipeId(@Bind("recipeId") Integer recipeId);

  /**
   * Retrieves recipe history entries within a specific date range.
   *
   * @param startDate The start date as an ISO-8601 string
   * @param endDate The end date as an ISO-8601 string
   * @return A list of RecipeHistory objects within the date range
   */
  @SqlQuery("""
      SELECT
        history_id as historyId,
        user_id as userId,
        recipe_id as recipeId,
        created_at as createdAt
      FROM recipe_history
      WHERE created_at BETWEEN :startDate AND :endDate
      ORDER BY created_at DESC
      """)
  List<RecipeHistory> getRecipeHistoryByDateRange(
      @Bind("startDate") String startDate,
      @Bind("endDate") String endDate
  );

  /**
   * Retrieves the most recent recipe history entries.
   *
   * @param limit The maximum number of entries to retrieve
   * @return A list of the most recent RecipeHistory objects
   */
  @SqlQuery("""
      SELECT
        history_id as historyId,
        user_id as userId,
        recipe_id as recipeId,
        created_at as createdAt
      FROM recipe_history
      ORDER BY created_at DESC
      LIMIT :limit
      """)
  List<RecipeHistory> getRecentRecipeHistory(@Bind("limit") int limit);

  /**
   * Retrieves the most recent recipe history entries for a specific user.
   *
   * @param userId The ID of the user
   * @param limit The maximum number of entries to retrieve
   * @return A list of the most recent RecipeHistory objects for the user
   */
  @SqlQuery("""
      SELECT
        history_id as historyId,
        user_id as userId,
        recipe_id as recipeId,
        created_at as createdAt
      FROM recipe_history
      WHERE user_id = :userId
      ORDER BY created_at DESC
      LIMIT :limit
      """)
  List<RecipeHistory> getRecentRecipeHistoryByUserId(
      @Bind("userId") Integer userId,
      @Bind("limit") int limit
  );

  /*DELETE*/
  /**
   * Deletes a recipe history entry from the database by its ID.
   *
   * @param historyId The ID of the history entry to delete
   * @return The number of rows affected (should be 1 if successful)
   */
  @SqlUpdate("""
      DELETE FROM recipe_history
      WHERE history_id = :id
      """)
  int deleteRecipeHistoryById(@Bind("id") Integer historyId);

  /**
   * Deletes all recipe history entries for a specific user.
   *
   * @param userId The ID of the user
   * @return The number of rows affected
   */
  @SqlUpdate("""
      DELETE FROM recipe_history
      WHERE user_id = :userId
      """)
  int deleteRecipeHistoryByUserId(@Bind("userId") Integer userId);

  /**
   * Deletes all recipe history entries for a specific recipe.
   *
   * @param recipeId The ID of the recipe
   * @return The number of rows affected
   */
  @SqlUpdate("""
      DELETE FROM recipe_history
      WHERE recipe_id = :recipeId
      """)
  int deleteRecipeHistoryByRecipeId(@Bind("recipeId") Integer recipeId);

  /**
   * Deletes recipe history entries older than a specified date.
   *
   * @param date The cutoff date as an ISO-8601 string
   * @return The number of rows affected
   */
  @SqlUpdate("""
      DELETE FROM recipe_history
      WHERE created_at < :date
      """)
  int deleteRecipeHistoryOlderThan(@Bind("date") String date);

  /**
   * Deletes multiple recipe history entries in a single batch operation.
   *
   * @param historyIds A list of history entry IDs to delete
   * @return An array of update counts (1 for each successful delete)
   */
  @SqlBatch("""
      DELETE FROM recipe_history
      WHERE history_id = ?
      """)
  int[] batchDeleteRecipeHistoryByIds(List<Integer> historyIds);

  /**
   * Checks if a recipe history entry exists for a specific user and recipe.
   *
   * @param userId The ID of the user
   * @param recipeId The ID of the recipe
   * @return true if an entry exists, false otherwise
   */
  @SqlQuery("""
      SELECT EXISTS(
        SELECT 1 FROM recipe_history
        WHERE user_id = :userId AND recipe_id = :recipeId
      )
      """)
  boolean recipeHistoryExists(
      @Bind("userId") Integer userId,
      @Bind("recipeId") Integer recipeId
  );

  /**
   * Counts the number of times a recipe has been viewed.
   *
   * @param recipeId The ID of the recipe
   * @return The number of views
   */
  @SqlQuery("""
      SELECT COUNT(*)
      FROM recipe_history
      WHERE recipe_id = :recipeId
      """)
  int countRecipeViews(@Bind("recipeId") Integer recipeId);

  /**
   * Retrieves the most popular recipes based on view count.
   *
   * @param limit The maximum number of recipes to retrieve
   * @return A list of recipe IDs ordered by popularity
   */
  @SqlQuery("""
      SELECT recipe_id
      FROM recipe_history
      GROUP BY recipe_id
      ORDER BY COUNT(*) DESC
      LIMIT :limit
      """)
  List<Integer> getMostPopularRecipeIds(@Bind("limit") int limit);
}