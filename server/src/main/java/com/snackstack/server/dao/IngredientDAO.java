package com.snackstack.server.dao;

import com.snackstack.server.model.Ingredient;
import java.util.List;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

/**
 * Data Access Object for the ingredients table.
 * Provides methods to create, read, update, and delete ingredient records.
 * Uses JDBI SQL object API for database operations.
 */
@RegisterBeanMapper(Ingredient.class)
public interface IngredientDAO {

  /*CREATE*/
  /**
   * Adds a new ingredient to the database.
   *
   * @param ingredientName The name of the ingredient to add
   * @return The ID of the newly created ingredient
   */
  @SqlUpdate("""
      INSERT INTO ingredients(ingredient_name)
      VALUES (:name)
      RETURNING ingredient_id
      """)
  Integer addIngredient(@Bind("name") String ingredientName);

  /**
   * Adds multiple ingredients to the database in a single batch operation.
   * This is more efficient than adding ingredients one by one.
   *
   * @param ingredientNames A list of ingredient names to add
   * @return An array of update counts (1 for each successful insert)
   */
  @SqlBatch("""
      INSERT INTO ingredients(ingredient_name)
      VALUES (?)
      """)
  int[] batchAddIngredients(List<String> ingredientNames);

  /*READ*/
  /**
   * Retrieves the name of an ingredient by its ID.
   *
   * @param ingredientId The ID of the ingredient to retrieve
   * @return The name of the ingredient
   */
  @SqlQuery("""
      SELECT ingredient_name
      FROM ingredients
      WHERE ingredient_id = :id
      """)
  String getIngredientNameById(@Bind("id") long ingredientId);


  @SqlQuery("""
      SELECT ingredient_id
      FROM ingredients
      WHERE ingredient_name = :name
      """)
  Integer getIngredientIdByName(@Bind("name") String name);
  /**
   * Retrieves multiple ingredients by their IDs.
   *
   * @param ingredientIds A list of ingredient IDs to retrieve
   * @return A list of Ingredient objects matching the given IDs
   */
  @SqlQuery("""
      SELECT *
      FROM ingredients
      WHERE ingredient_id IN (<ids>)
      """)
  List<Ingredient> getIngredientsByIds(@BindList("ids") List<Integer> ingredientIds);

  /**
   * Retrieves a single ingredient by its ID.
   *
   * @param ingredientId The ID of the ingredient to retrieve
   * @return The Ingredient object or null if not found
   */
  @SqlQuery("""
      SELECT *
      FROM ingredients
      WHERE ingredient_id = :id""")
  Ingredient getIngredientById(@Bind("id") Integer ingredientId);

  /*UPDATE*/
  /**
   * Updates the name of an existing ingredient.
   *
   * @param ingredientId The ID of the ingredient to update
   * @param ingredientName The new name for the ingredient
   * @return The number of rows affected (should be 1 if successful)
   */
  @SqlUpdate("""
      UPDATE ingredients
      SET ingredient_name = :name
      WHERE ingredient_id = :id
      """)
  int updateIngredient(@Bind("id") Integer ingredientId, @Bind("name") String ingredientName);


  /*DELETE*/
  /**
   * Deletes an ingredient from the database by its name.
   * Note: This might delete multiple ingredients if names are not unique.
   *
   * @param ingredientName The name of the ingredient to delete
   * @return The number of rows affected
   */
  @SqlUpdate("""
      DELETE FROM ingredients
      WHERE ingredient_name = :name
      """)
  int deleteIngredientByName(@Bind("name") String ingredientName);

  /**
   * Deletes an ingredient from the database by its ID.
   *
   * @param ingredientId The ID of the ingredient to delete
   * @return The number of rows affected (should be 1 if successful)
   */
  @SqlUpdate("""
      DELETE FROM ingredients
      WHERE ingredient_id = :id
      """)
  int deleteIngredientById(@Bind("id") Integer ingredientId);

  /**
   * Deletes multiple ingredients in a single batch operation.
   * This is more efficient than deleting ingredients one by one.
   *
   * @param ingredientIds A list of ingredient IDs to delete
   * @return An array of update counts (1 for each successful delete)
   */
  @SqlBatch("""
      DELETE FROM ingredients
      WHERE ingredient_id = ?
      """)
  int[] batchDeleteIngredientsByIds(List<Integer> ingredientIds);

  /**
   * Checks if an ingredient with the given name exists in the database.
   *
   * @param ingredientName The name of the ingredient to check
   * @return true if the ingredient exists, false otherwise
   */
  @SqlQuery("""
      SELECT EXISTS(
        SELECT 1 FROM ingredients
        WHERE ingredient_name = :name
      )
      """)
  boolean ingredientExists(@Bind("name") String ingredientName);
}