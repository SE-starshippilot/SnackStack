package com.snackstack.server.dao;

import com.snackstack.server.model.User;
import java.time.Instant;
import java.util.Optional;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.*;

@RegisterBeanMapper(User.class)
public interface UserDAO {

  /* CREATE: Create a new user */
  @SqlUpdate("""
      INSERT INTO users(user_name, email, created_at, last_login_at)
      VALUES (:userName, :email, :createdAt, :lastLoginAt)
      """)
  int insert(
      @Bind("userName") String userName,
      @Bind("email") String email,
      @Bind("createdAt") Instant createdAt,
      @Bind("lastLoginAt") Instant lastLoginAt
  );

  /* READ: fetch a user */
  @SqlQuery("""
      SELECT user_id
      FROM users
      WHERE user_name = :userName
      """)
  Optional<Integer> getUserIdByName(@Bind("userName") String username);

  /* DELETE: remove a user */
  @SqlUpdate("""
      DELETE FROM users
      WHERE user_name = :userName
      """)
  int deleteByName(@Bind("userName") String userName);

  /* CHECK: verify if a user exists by username */
  @SqlQuery("""
      SELECT EXISTS (
          SELECT 1 
          FROM users 
          WHERE user_name = :userName
      )
      """)
  boolean userExistsByName(@Bind("userName") String userName);

  /* CHECK: verify if a user exists by email */
  @SqlQuery("""
      SELECT EXISTS (
          SELECT 1
          FROM users 
          WHERE email = :email
      )
      """)
  boolean userExistsByEmail(@Bind("email") String email);

  /* CHECK: verify if a user exists by id */
  @SqlQuery("""
      SELECT EXISTS (
          SELECT 1 
          FROM users 
          WHERE user_id = :userId
      )
      """)
  boolean userExistsById(@Bind("userId") Integer userId);
}
