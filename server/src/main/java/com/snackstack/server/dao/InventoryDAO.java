package com.snackstack.server.dao;

import com.snackstack.server.model.InventoryItem;
import java.util.List;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.*;
import java.time.Instant;

/**
 * Data Access Object for the inventory_items table.
 * Provides methods to manage user inventory items in the database.
 * Uses JDBI SQL object API for database operations.
 */
@RegisterBeanMapper(InventoryItem.class)
public interface InventoryDAO {

  /*CREATE*/
  /**
   * Adds a single ingredient to a user's inventory.
   *
   * @param userId The ID of the user
   * @param ingredientId The ID of the ingredient to add
   * @param purchaseDate The timestamp when the item was purchased
   * @return The ID of the newly created inventory item
   */
  @SqlUpdate("""
      INSERT INTO inventory_items(user_id, ingredient_id, purchase_date)
      VALUES (:userId, :ingredientId, :purchaseDate)
      """)
  @GetGeneratedKeys
  Integer addInventoryItem(
      @Bind("userId") Integer userId,
      @Bind("ingredientId") Integer ingredientId,
      @Bind("purchaseDate") Instant purchaseDate
  );

  /**
   * Adds multiple ingredients to a user's inventory in a single batch operation.
   * All items will have the same purchase date.
   *
   * @param userId The ID of the user
   * @param ingredientIds A list of ingredient IDs to add
   * @param purchaseDate The timestamp when the items were purchased
   * @return An array of update counts (1 for each successful insert)
   */
  @SqlBatch("""
      INSERT INTO inventory_items(user_id, ingredient_id, purchase_date)
      VALUES (:userId, ?, :purchaseDate)
      """)
  int[] batchAddInventoryItems(
      @Bind("userId") Integer userId,
      List<Integer> ingredientIds,
      @Bind("purchaseDate") Instant purchaseDate
  );

  /*READ*/
  /**
   * Retrieves an inventory item by its ID.
   * Joins with the ingredients table to include the ingredient name.
   *
   * @param inventoryItemId The ID of the inventory item to retrieve
   * @return The InventoryItem object or null if not found
   */
  @SqlQuery("""
      SELECT i.inventory_item_id, i.user_id, i.ingredient_id,
             ing.ingredient_name, i.purchase_date
      FROM inventory_items i
      JOIN ingredients ing ON i.ingredient_id = ing.ingredient_id
      WHERE i.inventory_item_id = :id
      """)
  InventoryItem getInventoryItemById(@Bind("id") Integer inventoryItemId);

  /**
   * Retrieves all inventory items for a specific user.
   * Joins with the ingredients table to include ingredient names.
   *
   * @param userId The ID of the user
   * @return A list of InventoryItem objects belonging to the user
   */
  @SqlQuery("""
      SELECT ing.ingredient_name
      FROM inventory_items i
      JOIN ingredients ing ON i.ingredient_id = ing.ingredient_id
      WHERE i.user_id = :userId
      """)
  List<String> getUserInventoryItemNames(@Bind("userId") Integer userId);

  /**
   * Retrieves the IDs of all ingredients in a user's inventory.
   *
   * @param userId The ID of the user
   * @return A list of ingredient IDs in the user's inventory
   */
  @SqlQuery("""
      SELECT ingredient_id
      FROM inventory_items
      WHERE user_id = :userId
      """)
  List<Integer> getUserIngredientIds(@Bind("userId") Integer userId);

  /**
   * Retrieves the names of all ingredients in a user's inventory.
   *
   * @param userId The ID of the user
   * @return A list of ingredient names in the user's inventory
   */
  @SqlQuery("""
      SELECT ing.ingredient_name
      FROM inventory_items i
      JOIN ingredients ing ON i.ingredient_id = ing.ingredient_id
      WHERE i.user_id = :userId
      """)
  List<String> getUserIngredientNames(@Bind("userId") Integer userId);

  /**
   * Checks if a user has a specific ingredient in their inventory.
   *
   * @param userId The ID of the user
   * @param ingredientId The ID of the ingredient to check
   * @return true if the user has the ingredient, false otherwise
   */
  @SqlQuery("""
      SELECT EXISTS(
        SELECT 1 FROM inventory_items
        WHERE user_id = :userId AND ingredient_id = :ingredientId
      )
      """)
  boolean userHasIngredient(
      @Bind("userId") Integer userId,
      @Bind("ingredientId") Integer ingredientId
  );

  /**
   * Checks if a user has all the specified ingredients in their inventory.
   *
   * @param userId The ID of the user
   * @param ingredientIds A list of ingredient IDs to check
   * @param totalCount The expected count (should match the size of ingredientIds)
   * @return true if the user has all the ingredients, false otherwise
   */
  @SqlQuery("""
      SELECT COUNT(DISTINCT ingredient_id) = :totalCount
      FROM inventory_items
      WHERE user_id = :userId
        AND ingredient_id IN (<ingredientIds>)
      """)
  boolean userHasAllIngredients(
      @Bind("userId") Integer userId,
      @BindList("ingredientIds") List<Integer> ingredientIds,
      @Bind("totalCount") int totalCount
  );

  /*DELETE: Delete an item from database*/
  /**
   * Removes an ingredient from a user's inventory by ingredient name.
   * Note: This method relies on the ingredientName column which is not in this table,
   * so it might require a join or subquery in the actual implementation.
   *
   * @param userId The ID of the user
   * @param ingredientId The id of the ingredient to remove
   * @return The number of rows affected
   */
  @SqlUpdate("""
      DELETE FROM inventory_items
      WHERE user_id = :userId AND ingredient_id = :ingredientId
      
      """)
  int deleteRecordByUserAndIngredientId(
      @Bind("userId") Integer userId,
      @Bind("ingredientId") Integer ingredientId
  );

  /**
   * Deletes an inventory item by its ID.
   *
   * @param inventoryItemId The ID of the inventory item to delete
   * @return The number of rows affected (should be 1 if successful)
   */
  @SqlUpdate("""
      DELETE FROM inventory_items
      WHERE inventory_item_id = :id
      """)
  int deleteInventoryItem(@Bind("id") Integer inventoryItemId);

  /**
   * Removes a specific ingredient from a user's inventory.
   *
   * @param userId The ID of the user
   * @param ingredientId The ID of the ingredient to remove
   * @return The number of rows affected
   */
  @SqlUpdate("""
      DELETE FROM inventory_items
      WHERE user_id = :userId AND ingredient_id = :ingredientId
      """)
  int deleteUserIngredient(
      @Bind("userId") Integer userId,
      @Bind("ingredientId") Integer ingredientId
  );

  /**
   * Removes multiple ingredients from a user's inventory in a single batch operation.
   *
   * @param userId The ID of the user
   * @param ingredientIds A list of ingredient IDs to remove
   * @return An array of update counts (representing rows affected for each delete)
   */
  @SqlBatch("""
      DELETE FROM inventory_items
      WHERE user_id = :userId AND ingredient_id = ?
      """)
  int[] batchDeleteUserIngredients(
      @Bind("userId") Integer userId,
      List<Integer> ingredientIds
  );

  /**
   * Removes all items from a user's inventory.
   *
   * @param userId The ID of the user
   * @return The number of rows affected (total items removed)
   */
  @SqlUpdate("""
      DELETE FROM inventory_items
      WHERE user_id = :userId
      """)
  int clearUserInventory(@Bind("userId") Integer userId);

  /**
   * Counts the number of items in a user's inventory.
   *
   * @param userId The ID of the user
   * @return The total count of inventory items
   */
  @SqlQuery("""
      SELECT COUNT(*)
      FROM inventory_items
      WHERE user_id = :userId
      """)
  int countUserInventoryItems(@Bind("userId") Integer userId);
}