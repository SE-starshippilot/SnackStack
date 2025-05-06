package com.snackstack.server.dao;

import com.snackstack.server.model.RecipeIngredient;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

@RegisterConstructorMapper(RecipeIngredient.class)
public interface RecipeIngredientDAO {

  // insert a recipe-ingredient relationship
  @SqlUpdate("""
        INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit, note)
        VALUES (:recipeId, :ingredientId, :quantity, :unit, :note)
    """)
  void insert(
      @Bind("recipeId") Integer recipeId,
      @Bind("ingredientId") Integer ingredientId,
      @Bind("quantity") Integer quantity,
      @Bind("unit") String unit,
      @Bind("note") String note
  );

  // delete record based on given recipe_id and ingredient_id
  @SqlUpdate("""
        DELETE FROM recipe_ingredients
        WHERE recipe_id = :recipeId AND ingredient_id = :ingredientId
    """)
  int delete(
      @Bind("recipeId") Integer recipeId,
      @Bind("ingredientId") Integer ingredientId
  );

  //  delete record based on given recipe_id
  @SqlUpdate("""
        DELETE FROM recipe_ingredients
        WHERE recipe_id = :recipeId
    """)
  int deleteAllByRecipeId(@Bind("recipeId") Integer recipeId);

  // get all ingredient given recipe_id
  @SqlQuery("""
        SELECT * FROM recipe_ingredients
        WHERE recipe_id = :recipeId
    """)
  List<RecipeIngredient> findByRecipeId(@Bind("recipeId") Integer recipeId);
}
