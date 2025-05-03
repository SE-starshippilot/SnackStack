package com.snackstack.server.dao;

import com.snackstack.server.model.InventoryItem;
import java.util.List;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.*;
import java.time.Instant;

@RegisterBeanMapper(InventoryItem.class)
public interface InventoryDAO {

  /*CREATE: Create a new item in database*/
  @SqlQuery("""
      INSERT INTO inventory_items(user_id, ingredient_name, purchase_date)
      VALUES (:userId, :ingredientName, :purchaseDate)
      RETURNING inventory_item_id
      """)
  Integer insert(
      @Bind("userId") Integer userId,
      @Bind("ingredientName") String ingredientName,
      @Bind("purchaseDate") Instant purchaseDate
  );

  /* RETRIEVE: Acquire all items of a given user*/
  @SqlQuery("""
      SELECT ingredient_name
      FROM inventory_items
      WHERE user_id = :userId
      """)
  List<String> getAllIngredientsOfUser(
      @Bind("userId") Integer userId
  );

  /*DELETE: Delete an item from database*/
  @SqlUpdate("""
      DELETE FROM inventory_items
      WHERE user_id = :userId AND ingredient_name = :ingredientName
      """)
  int deleteIngredient(
      @Bind("userId") Integer userId,
      @Bind("ingredientName") String ingredientName
  );
}
//