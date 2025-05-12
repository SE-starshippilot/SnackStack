package com.snackstack.server.dao;

import com.snackstack.server.dto.RecipeRequestDTO;
import com.snackstack.server.model.RecipeRequest;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.*;

import java.util.*;

/**
 * Handles recipe_requests plus the “child” tables recipe_request_origins
 * and recipe_request_allergies.
 *
 * All write-operations are wrapped in one DB transaction to guarantee
 * consistency.
 */
@RegisterBeanMapper(RecipeRequest.class)
public interface RecipeRequestDAO {

  /* -------------------------------------------------------------------- */
  /* Low-level statements (package-private)                                */
  /* -------------------------------------------------------------------- */

  /* Insert the parent row and return the generated PK */
  @SqlUpdate("""
      INSERT INTO recipe_requests (user_id, servings, recipe_type)
      VALUES (:userId, :servings, :recipeType)
      """)
  @GetGeneratedKeys("recipe_request_id")
  int _insertCore(
      @Bind("userId")    int    userId,
      @Bind("servings")  int    servings,
      @Bind("recipeType")String recipeType
  );

  /* Batch-insert all origins belonging to one request */
  @SqlBatch("""
      INSERT INTO recipe_request_origins (recipe_request_id, origin_name)
      VALUES (:requestId, :originName)
      """)
  void _insertOrigins(
      @Bind("requestId")  List<Integer> requestIds,
      @Bind("originName") List<String>  origins
  );

  /* Batch-insert all allergies belonging to one request */
  @SqlBatch("""
      INSERT INTO recipe_request_allergies (recipe_request_id, allergy_name)
      VALUES (:requestId, :allergyName)
      """)
  void _insertAllergies(
      @Bind("requestId")   List<Integer> requestIds,
      @Bind("allergyName") List<String>  allergies
  );

  /* -------------------------------------------------------------------- */
  /* Public facade methods                                                 */
  /* -------------------------------------------------------------------- */

  /**
   * Persist the complete request (core row + all origins + allergies)
   * inside a single transaction and return the generated request id.
   */
  @Transaction
  default int createRequest(int userId, RecipeRequestDTO dto) {

    int requestId = _insertCore(userId,
        dto.servings(),
        dto.meal_type());

    /* helper list: [requestId, requestId, …] same length as child rows */
    List<Integer> ids;

    /* origins --------------------------------------------------------- */
    if (dto.meal_origin() != null && !dto.meal_origin().isEmpty()) {
      ids = Collections.nCopies(dto.meal_origin().size(), requestId);
      _insertOrigins(ids, dto.meal_origin());
    }

    /* allergies ------------------------------------------------------- */
    if (dto.allergies() != null && !dto.allergies().isEmpty()) {
      ids = Collections.nCopies(dto.allergies().size(), requestId);
      _insertAllergies(ids, dto.allergies());
    }

    //  ingredients are not stored yet – add similar batch method if needed
    return requestId;
  }

  /* READ: one request */
  @SqlQuery("""
      SELECT recipe_request_id,
             user_id,
             servings,
             recipe_type,
             created_at
      FROM recipe_requests
      WHERE recipe_request_id = :requestId
      """)
  Optional<RecipeRequest> findById(@Bind("requestId") int requestId);

  /* READ: all requests of a user (newest first) */
  @SqlQuery("""
      SELECT recipe_request_id,
             user_id,
             servings,
             recipe_type,
             created_at
      FROM recipe_requests
      WHERE user_id = :userId
      ORDER BY created_at DESC
      """)
  List<RecipeRequest> findAllByUser(@Bind("userId") int userId);

  /* DELETE: remove one request (children will cascade) */
  @SqlUpdate("""
      DELETE FROM recipe_requests
      WHERE recipe_request_id = :requestId
        AND user_id            = :userId
      """)
  int deleteById(@Bind("requestId") int requestId,
      @Bind("userId")    int userId);
}