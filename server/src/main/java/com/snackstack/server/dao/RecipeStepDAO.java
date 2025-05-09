package com.snackstack.server.dao;

import com.snackstack.server.model.RecipeStep;
import java.util.List;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

@RegisterConstructorMapper(RecipeStep.class)
public interface RecipeStepDAO {

  @SqlUpdate("""
      INSERT INTO recipe_steps(recipe_id, step_number, step_description)
      VALUES (:recipeId, :stepNumber, :stepDescription)
      """)
  Integer addStep(
      @Bind("recipeId") int recipeId,
      @Bind("stepNumber") int stepNumber,
      @Bind("stepDescription") String stepDescription
  );

  @SqlBatch("""
      INSERT INTO recipe_steps(recipe_id, step_number, step_description)
      VALUES (:recipeId, :stepNumber, :stepDescription)
      """)
  void addStepsForRecipe(
      @Bind("recipeId") int recipeId,
      @Bind("stepNumber") Iterable<Integer> stepNumbers,
      @Bind("stepDescription") Iterable<String> stepDescriptions
  );

  @SqlUpdate("""
      DELETE FROM recipe_steps
      WHERE recipe_id = :recipeId
      """)
  void deleteStepsForRecipe(
      @Bind("recipeId") int recipeId
  );

  @SqlQuery("""
    SELECT * FROM recipe_steps
    WHERE recipe_id = :recipeId
    ORDER BY step_number ASC
    """)
  List<RecipeStep> getStepsForRecipe(@Bind("recipeId") int recipeId);
}
