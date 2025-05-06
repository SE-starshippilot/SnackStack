package com.snackstack.server.dao;

import com.snackstack.server.model.Ingredient;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;


//@RegisterBeanMapper(Ingredient.class)
@RegisterConstructorMapper(Ingredient.class)
public interface IngredientDAO {

  // Create a new ingredient in database
  @SqlQuery("""
      INSERT INTO ingredients(ingredient_id, ingredient_name)
      VALUES (:ingredientId, :ingredientName)
      RETURNING ingredient_id
      """)
  Integer insert(
      @Bind("ingredientId") Integer ingredientId,
      @Bind("ingredientName") String ingredientName
  );

  // Read a new ingredient in database
  @SqlQuery("""
        SELECT ingredient_id, ingredient_name FROM ingredients WHERE ingredient_id = :ingredientId
    """)
  Ingredient findById(@Bind("ingredientId") Integer ingredientId);

  // Read all
  @SqlQuery("""
        SELECT ingredient_id, ingredient_name FROM ingredients
    """)
  List<Ingredient> findAll();

  // Update ingredient name
  @SqlUpdate("""
        UPDATE ingredients
        SET ingredient_name = :ingredientName
        WHERE ingredient_id = :ingredientId
    """)
  int update(
      @Bind("ingredientId") Integer ingredientId,
      @Bind("ingredientName") String ingredientName
  );

  // Delete (by ID)
  @SqlUpdate("""
        DELETE FROM ingredients WHERE ingredient_id = :ingredientId
    """)
  int deleteById(@Bind("ingredientId") Integer ingredientId);
}
