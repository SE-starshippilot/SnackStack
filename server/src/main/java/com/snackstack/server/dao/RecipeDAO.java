package com.snackstack.server.dao;

import com.snackstack.server.model.Recipe;
import com.snackstack.server.model.RecipeType;
import java.util.List;
import org.jdbi.v3.core.mapper.EnumByNameMapperFactory;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.config.RegisterColumnMapperFactory;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

/**
 * DAO for the {@code recipes} table.
 */
@RegisterConstructorMapper(Recipe.class)
@RegisterColumnMapperFactory(EnumByNameMapperFactory.class)   // lets JDBI map RecipeType <‑‑> recipe_type
public interface RecipeDAO {

  /* ---------- CREATE ---------- */

  @SqlUpdate("""
      INSERT INTO recipes (recipe_name,
                           description,
                           servings,
                           recipe_origin_id,
                           recipe_type,
                           uuid)
      VALUES (:name,
              :description,
              :servings,
              :originId,
              :type::recipe_type,
              :uuid)
      """)
  @GetGeneratedKeys("recipe_id")
  int addRecipe(@Bind("name")        String      recipeName,
      @Bind("description") String      description,
      @Bind("servings")    int         servings,
      @Bind("originId")    String      recipeOriginId,
      @Bind("type")        RecipeType  recipeType,
      @Bind("uuid")        String      uuid);


  /* ---------- READ ---------- */

  @SqlQuery("""
      SELECT recipe_id                     AS id,
             recipe_name                   AS recipeName,
             description,
             servings,
             recipe_origin_id              AS recipeOrigin,
             recipe_type::text             AS recipeType,
             is_favorite                   AS isFavorite,
             uuid
        FROM recipes
       WHERE recipe_id = :id
      """)
  Recipe getRecipeById(@Bind("id") Integer recipeId);


  @SqlQuery("""
      SELECT recipe_id                     AS id,
             recipe_name                   AS recipeName,
             description,
             servings,
             recipe_origin_id              AS recipeOrigin,
             recipe_type::text             AS recipeType,
             is_favorite                   AS isFavorite,
             uuid
        FROM recipes
       WHERE uuid = :uuid
      """)
  Recipe getRecipeByUuid(@Bind("uuid") String uuid);


  @SqlQuery("""
      SELECT recipe_id                     AS recipeId,
             recipe_name                   AS recipeName,
             description,
             servings,
             recipe_origin_id              AS recipeOrigin,
             recipe_type::text             AS recipeType,
             is_favorite                   AS isFavorite,
             uuid
        FROM recipes
      """)
  List<Recipe> getAllRecipes();


  @SqlQuery("""
      SELECT recipe_id                     AS id,
             recipe_name                   AS recipeName,
             description,
             servings,
             recipe_origin_id              AS recipeOrigin,
             recipe_type::text             AS recipeType,
             is_favorite                   AS isFavorite,
             uuid
        FROM recipes
       WHERE recipe_id IN (<ids>)
      """)
  List<Recipe> getRecipesByIds(@BindList("ids") List<Integer> recipeIds);


  @SqlQuery("""
      SELECT recipe_id                     AS id,
             recipe_name                   AS recipeName,
             description,
             servings,
             recipe_origin_id              AS recipeOrigin,
             recipe_type::text             AS recipeType,
             is_favorite                   AS isFavorite,
             uuid
        FROM recipes
       WHERE recipe_type = :type
      """)
  List<Recipe> getRecipesByType(@Bind("type") RecipeType recipeType);


  @SqlQuery("""
      SELECT recipe_id                     AS id,
             recipe_name                   AS recipeName,
             description,
             servings,
             recipe_origin_id              AS recipeOrigin,
             recipe_type::text             AS recipeType,
             is_favorite                   AS isFavorite,
             uuid
        FROM recipes
       WHERE is_favorite = true
      """)
  List<Recipe> getFavoriteRecipes();


  /**
   * Find recipes that:
   *  • only use *availableIngredients*
   *  • do **not** contain any of the *allergies*
   *  • match the requested *recipeType* (nullable)
   *  • match one of the requested *recipeOrigin* values (nullable / empty ⇒ no filter)
   *
   * This query is designed to be driven directly by {@link com.snackstack.server.dto.RecipeGenerationDTO}.
   */
  @SqlQuery("""
      SELECT r.recipe_id                   AS id,
             r.recipe_name                 AS recipeName,
             r.description,
             r.servings,
             r.recipe_origin_id            AS recipeOrigin,
             r.recipe_type::text           AS recipeType,
             r.is_favorite                 AS isFavorite,
             r.uuid
        FROM recipes r
        JOIN recipe_ingredients ri USING (recipe_id)
        JOIN ingredients        ing USING (ingredient_id)

       /* — exclude recipes that contain a disallowed allergen — */
       WHERE NOT EXISTS (
               SELECT 1
                 FROM ingredient_allergent ia
                 JOIN alergents a ON ia.alergent_id = a.alergent_id
                WHERE ia.ingredient_id = ri.ingredient_id
                  AND a.alergent_name  = ANY(:allergies)
             )

         AND ( :type    IS NULL OR r.recipe_type      = :type )
         AND ( :origins IS NULL OR r.recipe_origin_id = ANY(:origins) )

       GROUP BY r.recipe_id
      HAVING COUNT(*) FILTER (WHERE ing.ingredient_name NOT IN (<ingredients>)) = 0
      """)
  List<Recipe> findRecipesForGeneration(
      @BindList(value = "ingredients",
          onEmpty = BindList.EmptyHandling.NULL_STRING) List<String> availableIngredients,
      @Bind("type")    RecipeType               mealType,
      @BindList(value = "origins",
          onEmpty = BindList.EmptyHandling.NULL_STRING) List<String> mealOrigins,
      @BindList(value = "allergies",
          onEmpty = BindList.EmptyHandling.NULL_STRING) List<String> allergies);


  /* ---------- UPDATE ---------- */

  @SqlUpdate("""
      UPDATE recipes
         SET recipe_name      = :name,
             description      = :description,
             servings         = :servings,
             recipe_origin_id = :originId,
             recipe_type      = :type,
             is_favorite      = :favorite,
             uuid             = :uuid
       WHERE recipe_id        = :id
      """)
  int updateRecipe(@Bind("id")         Integer     recipeId,
      @Bind("name")       String      recipeName,
      @Bind("description")String      description,
      @Bind("servings")   int         servings,
      @Bind("originId")   String      recipeOriginId,
      @Bind("type")       RecipeType  recipeType,
      @Bind("favorite")   boolean     isFavorite,
      @Bind("uuid")       String      uuid);


  @SqlUpdate("""
      UPDATE recipes
         SET is_favorite = :favorite
       WHERE recipe_id   = :id
      """)
  int updateFavoriteStatus(@Bind("id") Integer recipeId,
      @Bind("favorite") boolean isFavorite);


  /* ---------- DELETE ---------- */

  @SqlUpdate("DELETE FROM recipes WHERE recipe_id = :id")
  int deleteRecipeById(@Bind("id") Integer recipeId);


  @SqlUpdate("DELETE FROM recipes WHERE uuid = :uuid")
  int deleteRecipeByUuid(@Bind("uuid") String uuid);


  @SqlBatch("DELETE FROM recipes WHERE recipe_id = ?")
  int[] batchDeleteRecipesByIds(List<Integer> recipeIds);


  /* ---------- EXISTENCE / SEARCH ---------- */

  @SqlQuery("""
      SELECT EXISTS(
        SELECT 1 FROM recipes WHERE recipe_name = :name
      )
      """)
  boolean recipeExists(@Bind("name") String recipeName);


  @SqlQuery("""
      SELECT EXISTS(
        SELECT 1 FROM recipes WHERE uuid = :uuid
      )
      """)
  boolean recipeExistsByUuid(@Bind("uuid") String uuid);


  @SqlQuery("""
      SELECT recipe_id                     AS id,
             recipe_name                   AS recipeName,
             description,
             servings,
             recipe_origin_id              AS recipeOrigin,
             recipe_type::text             AS recipeType,
             is_favorite                   AS isFavorite,
             uuid
        FROM recipes
       WHERE (recipe_name ILIKE '%' || :term || '%'
              OR description ILIKE '%' || :term || '%')
      """)
  List<Recipe> searchRecipes(@Bind("term") String searchTerm);
}
