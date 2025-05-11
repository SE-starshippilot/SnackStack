package com.snackstack.server.dao;

import com.snackstack.server.model.Recipe;
import java.util.List;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

/**
 * Data Access Object for the recipes table.
 * Provides methods to create, read, update, and delete recipe records.
 * Uses JDBI SQL object API for database operations.
 */
@RegisterConstructorMapper(Recipe.class)
public interface RecipeDAO {

  /*CREATE*/
  /**
   * Adds a new recipe to the database.
   *
   * @param recipeName The name of the recipe to add
   * @param description The description of the recipe
   * @param servings The number of servings
   * @param recipeOriginId The origin ID of the recipe
   * @param recipeType The type of the recipe
   * @param uuid The UUID of the recipe
   * @return The ID of the newly created recipe
   */
  @SqlUpdate("""
      INSERT INTO recipes(recipe_name, description, servings, recipe_origin_id, recipe_type, uuid)
      VALUES (:name, :description, :servings, :originId, :type, :uuid)
      """)
  @GetGeneratedKeys("recipe_id")
  int addRecipe(
      @Bind("name") String recipeName,
      @Bind("description") String description,
      @Bind("servings") int servings,
      @Bind("originId") String recipeOriginId,
      @Bind("type") String recipeType,
      @Bind("uuid") String uuid
  );

  /*READ*/
  /**
   * Retrieves a single recipe by its ID.
   *
   * @param recipeId The ID of the recipe to retrieve
   * @return The Recipe object or null if not found
   */
  @SqlQuery("""
      SELECT 
        recipe_id as id,
        recipe_name as recipeName,
        description,
        servings,
        recipe_origin_id as recipeOrigin,
        recipe_type as recipeType,
        is_favorite as isFavorite,
        uuid
      FROM recipes
      WHERE recipe_id = :id
      """)
  Recipe getRecipeById(@Bind("id") Integer recipeId);

  /**
   * Retrieves a single recipe by its UUID.
   *
   * @param uuid The UUID of the recipe to retrieve
   * @return The Recipe object or null if not found
   */
  @SqlQuery("""
      SELECT 
        recipe_id as id,
        recipe_name as recipeName,
        description,
        servings,
        recipe_origin_id as recipeOrigin,
        recipe_type as recipeType,
        is_favorite as isFavorite,
        uuid
      FROM recipes
      WHERE uuid = :uuid
      """)
  Recipe getRecipeByUuid(@Bind("uuid") String uuid);

  /**
   * Retrieves all recipes from the database.
   *
   * @return A list of all Recipe objects
   */
  @SqlQuery("""
      SELECT
        recipe_id as id,
        recipe_name as recipeName,
        description,
        servings,
        recipe_origin_id as recipeOrigin,
        recipe_type as recipeType,
        is_favorite as isFavorite,
        uuid
      FROM recipes
      """)
  List<Recipe> getAllRecipes();

  /**
   * Retrieves multiple recipes by their IDs.
   *
   * @param recipeIds A list of recipe IDs to retrieve
   * @return A list of Recipe objects matching the given IDs
   */
  @SqlQuery("""
      SELECT 
        recipe_id as id,
        recipe_name as recipeName,
        description,
        servings,
        recipe_origin_id as recipeOrigin,
        recipe_type as recipeType,
        is_favorite as isFavorite,
        uuid
      FROM recipes
      WHERE recipe_id IN (<ids>)
      """)
  List<Recipe> getRecipesByIds(@BindList("ids") List<Integer> recipeIds);

  /**
   * Retrieves recipes by type.
   *
   * @param recipeType The type of recipes to retrieve
   * @return A list of Recipe objects of the specified type
   */
  @SqlQuery("""
      SELECT 
        recipe_id as id,
        recipe_name as recipeName,
        description,
        servings,
        recipe_origin_id as recipeOrigin,
        recipe_type as recipeType,
        is_favorite as isFavorite,
        uuid
      FROM recipes
      WHERE recipe_type = :type
      """)
  List<Recipe> getRecipesByType(@Bind("type") String recipeType);

  /**
   * Retrieves all favorite recipes.
   *
   * @return A list of Recipe objects marked as favorite
   */
  @SqlQuery("""
      SELECT 
        recipe_id as id,
        recipe_name as recipeName,
        description,
        servings,
        recipe_origin_id as recipeOrigin,
        recipe_type as recipeType,
        is_favorite as isFavorite,
        uuid
      FROM recipes
      WHERE is_favorite = true
      """)
  List<Recipe> getFavoriteRecipes();

  /*UPDATE*/
  /**
   * Updates an existing recipe.
   *
   * @param recipeId The ID of the recipe to update
   * @param recipeName The new name for the recipe
   * @param description The new description for the recipe
   * @param servings The new number of servings
   * @param recipeOriginId The new origin ID of the recipe
   * @param recipeType The new type of the recipe
   * @param isFavorite The new favorite status
   * @param uuid The UUID of the recipe
   * @return The number of rows affected (should be 1 if successful)
   */
  @SqlUpdate("""
      UPDATE recipes
      SET recipe_name = :name,
          description = :description,
          servings = :servings,
          recipe_origin_id = :originId,
          recipe_type = :type,
          is_favorite = :favorite,
          uuid = :uuid
      WHERE recipe_id = :id
      """)
  int updateRecipe(
      @Bind("id") Integer recipeId,
      @Bind("name") String recipeName,
      @Bind("description") String description,
      @Bind("servings") int servings,
      @Bind("originId") String recipeOriginId,
      @Bind("type") String recipeType,
      @Bind("favorite") boolean isFavorite,
      @Bind("uuid") String uuid
  );

  /**
   * Updates the favorite status of a recipe.
   *
   * @param recipeId The ID of the recipe to update
   * @param isFavorite The new favorite status
   * @return The number of rows affected (should be 1 if successful)
   */
  @SqlUpdate("""
      UPDATE recipes
      SET is_favorite = :favorite
      WHERE recipe_id = :id
      """)
  int updateFavoriteStatus(@Bind("id") Integer recipeId, @Bind("favorite") boolean isFavorite);

  /*DELETE*/
  /**
   * Deletes a recipe from the database by its ID.
   *
   * @param recipeId The ID of the recipe to delete
   * @return The number of rows affected (should be 1 if successful)
   */
  @SqlUpdate("""
      DELETE FROM recipes
      WHERE recipe_id = :id
      """)
  int deleteRecipeById(@Bind("id") Integer recipeId);

  /**
   * Deletes a recipe from the database by its UUID.
   *
   * @param uuid The UUID of the recipe to delete
   * @return The number of rows affected (should be 1 if successful)
   */
  @SqlUpdate("""
      DELETE FROM recipes
      WHERE uuid = :uuid
      """)
  int deleteRecipeByUuid(@Bind("uuid") String uuid);

  /**
   * Deletes multiple recipes in a single batch operation.
   * This is more efficient than deleting recipes one by one.
   *
   * @param recipeIds A list of recipe IDs to delete
   * @return An array of update counts (1 for each successful delete)
   */
  @SqlBatch("""
      DELETE FROM recipes
      WHERE recipe_id = ?
      """)
  int[] batchDeleteRecipesByIds(List<Integer> recipeIds);

  /**
   * Checks if a recipe with the given name exists in the database.
   *
   * @param recipeName The name of the recipe to check
   * @return true if the recipe exists, false otherwise
   */
  @SqlQuery("""
      SELECT EXISTS(
        SELECT 1 FROM recipes
        WHERE recipe_name = :name
      )
      """)
  boolean recipeExists(@Bind("name") String recipeName);

  /**
   * Checks if a recipe with the given UUID exists in the database.
   *
   * @param uuid The UUID of the recipe to check
   * @return true if the recipe exists, false otherwise
   */
  @SqlQuery("""
      SELECT EXISTS(
        SELECT 1 FROM recipes
        WHERE uuid = :uuid
      )
      """)
  boolean recipeExistsByUuid(@Bind("uuid") String uuid);

  /**
   * Retrieves recipes by a search term in the name or description.
   *
   * @param searchTerm The term to search for
   * @return A list of Recipe objects matching the search term
   */
  @SqlQuery("""
      SELECT 
        recipe_id as id,
        recipe_name as recipeName,
        description,
        servings,
        recipe_origin_id as recipeOrigin,
        recipe_type as recipeType,
        is_favorite as isFavorite,
        uuid
      FROM recipes
      WHERE 
        recipe_name ILIKE '%' || :term || '%' OR 
        description ILIKE '%' || :term || '%'
      """)
  List<Recipe> searchRecipes(@Bind("term") String searchTerm);
}