package com.snackstack.server.dao;

import com.snackstack.server.model.RecipeSteps;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.SqlQuery;

import java.util.List;

@RegisterConstructorMapper(RecipeSteps.class)
public interface RecipeStepsDAO {
  // insert a step
  @SqlUpdate("""
        INSERT INTO recipe_steps (recipe_id, step_number, step_description)
        VALUES (:recipeId, :stepNumber, :stepDescription)
    """)
  void insert(
      @Bind("recipeId") Integer recipeId,
      @Bind("stepNumber") Integer stepNumber,
      @Bind("stepDescription") String stepDescription
  );

  // delete a step based on given recipe_id
  @SqlUpdate("""
        DELETE FROM recipe_steps
        WHERE recipe_id = :recipeId AND step_number = :stepNumber
    """)
  int delete(
      @Bind("recipeId") Integer recipeId,
      @Bind("stepNumber") Integer stepNumber
  );

  // delete all steps for a certain recipe
  @SqlUpdate("""
        DELETE FROM recipe_steps
        WHERE recipe_id = :recipeId
    """)
  int deleteAllByRecipeId(@Bind("recipeId") Integer recipeId);

  // get all steps for a recipe (sorted by step_number)
  @SqlQuery("""
        SELECT * FROM recipe_steps
        WHERE recipe_id = :recipeId
        ORDER BY step_number
    """)
  List<RecipeSteps> findByRecipeId(@Bind("recipeId") Integer recipeId);
}
