package com.snackstack.server.dao;

import com.snackstack.server.model.Recipe;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

@RegisterConstructorMapper(Recipe.class)
public interface RecipesDAO {

  // insert a new recipe, and return recipe_id
  @SqlQuery("""
        INSERT INTO recipes (recipe_name, description, servings, recipe_origin_id, recipe_type)
        VALUES (:name, :desc, :servings, :originId, :type)
        RETURNING recipe_id
    """)
  Integer insert(
      @Bind("name") String recipeName,
      @Bind("desc") String description,
      @Bind("servings") Integer servings,
      @Bind("originId") String recipeOriginId,
      @Bind("type") String recipeType
  );

  // delete recipe based on recipe_id
  @SqlUpdate("""
        DELETE FROM recipes
        WHERE recipe_id = :id
    """)
  int deleteById(@Bind("id") Integer recipeId);

  // get all
  @SqlQuery("SELECT * FROM recipes")
  List<Recipe> findAll();
}
