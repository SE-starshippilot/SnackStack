package com.snackstack.server.dao;

import com.snackstack.server.model.RecipeIngredient;
import java.util.List;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

@RegisterConstructorMapper(RecipeIngredient.class)
public interface RecipeIngredientDAO {
  /**
   * Adds a single ingredient to a recipe.
   *
   * @param recipeId The ID of the recipe
   * @param ingredientId The ID of the ingredient
   * @param quantity The quantity of the ingredient
   * @param unit The unit of measurement (optional)
   * @param note Additional notes about the ingredient (optional)
   * @return The number of rows affected (should be 1 if successful)
   */
  @SqlUpdate("""
        INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit, note)
        VALUES (:recipeId, :ingredientId, :quantity, :unit, :note)
        """)
  Integer addIngredientToRecipe(
      @Bind("recipeId") Integer recipeId,
      @Bind("ingredientId") Integer ingredientId,
      @Bind("quantity") Float quantity,
      @Bind("unit") String unit,
      @Bind("note") String note
  );

  /**
   * Batch adds multiple ingredients to a recipe.
   * This method should be called by a service layer that handles
   * the conversion between IngredientDTO and ingredient_id.
   *
   * @param recipeId The ID of the recipe (same for all batch items)
   * @param ingredientIds List of ingredient IDs
   * @param quantities List of quantities
   * @param units List of units
   * @param notes List of notes
   * @return An array of update counts
   */
  @SqlBatch("""
        INSERT INTO recipe_ingredients (recipe_id, ingredient_id, quantity, unit, note)
        VALUES (:recipeId, ?, ?, ?, ?)
        """)
  int[] batchAddIngredientsToRecipe(
      @Bind("recipeId") int recipeId,
      List<Integer> ingredientIds,
      List<Float> quantities,
      List<String> units,
      List<String> notes
  );

  /**
   * Retrieves all ingredients for a specific recipe, including their quantities,
   * units, and any notes.
   *
   * @param recipeId The ID of the recipe
   * @return A list of RecipeIngredient objects containing all ingredient details
   */
  @SqlQuery("""
        SELECT *
        FROM recipe_ingredients ri
        WHERE ri.recipe_id = :recipeId
        """)
  List<RecipeIngredient> getIngredientsForRecipe(@Bind("recipeId") Integer recipeId);
}
